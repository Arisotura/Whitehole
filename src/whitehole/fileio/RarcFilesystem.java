/*
    Copyright 2012 The Whitehole team

    This file is part of Whitehole.

    Whitehole is free software: you can redistribute it and/or modify it under
    the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    Whitehole is distributed in the hope that it will be useful, but WITHOUT ANY 
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
    FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along 
    with Whitehole. If not, see http://www.gnu.org/licenses/.
*/

package whitehole.fileio;

import java.io.*;
import java.util.*;

public class RarcFilesystem implements FilesystemBase
{
    public RarcFilesystem(FileBase _file) throws IOException
    {
        file = new Yaz0File(_file);
        file.setBigEndian(true);

        file.position(0);
        int tag = file.readInt();
        if (tag != 0x52415243) 
            throw new IOException(String.format("File isn't a RARC (tag 0x%1$08X, expected 0x52415243)", tag));

        file.position(0xC);
        fileDataOffset = file.readInt() + 0x20;
        file.position(0x20);
        numDirNodes = file.readInt();
        dirNodesOffset = file.readInt() + 0x20;
        file.skip(0x4);
        fileEntriesOffset = file.readInt() + 0x20;
        file.skip(0x4);
        stringTableOffset = file.readInt() + 0x20;

        dirEntries = new LinkedHashMap<>();
        fileEntries = new LinkedHashMap<>();

        DirEntry root = new DirEntry();
        root.ID = 0;
        root.parentDir = 0xFFFFFFFF;

        file.position(dirNodesOffset + 0x6);
        int rnoffset = file.readShort();
        file.position(stringTableOffset + rnoffset);
        root.name = file.readString("ASCII", 0);
        root.fullName = "/" + root.name;

        dirEntries.put(0, root);

        for (int i = 0; i < numDirNodes; i++)
        {
            DirEntry parentdir = dirEntries.get(i);

            file.position(dirNodesOffset + (i * 0x10) + 10);

            short numentries = file.readShort();
            int firstentry = file.readInt();

            for (int j = 0; j < numentries; j++)
            {
                int entryoffset = fileEntriesOffset + ((j + firstentry) * 0x14);
                file.position(entryoffset);

                int fileid = file.readShort() & 0xFFFF;
                file.skip(4);
                int nameoffset = file.readShort() & 0xFFFF;
                int dataoffset = file.readInt();
                int datasize = file.readInt();

                file.position(stringTableOffset + nameoffset);
                String name = file.readString("ASCII", 0);
                if (name.equals(".") || name.equals("..")) continue;

                String fullname = parentdir.fullName + "/" + name;

                if (fileid == 0xFFFF)
                {
                    DirEntry d = new DirEntry();
                    d.entryOffset = entryoffset;
                    d.ID = dataoffset;
                    d.parentDir = i;
                    d.nameOffset = nameoffset;
                    d.name = name;
                    d.fullName = fullname;

                    dirEntries.put(dataoffset, d);
                }
                else
                {
                    FileEntry f = new FileEntry();
                    f.entryOffset = entryoffset;
                    f.ID = fileid;
                    f.parentDir = i;
                    f.nameOffset = nameoffset;
                    f.dataOffset = dataoffset;
                    f.dataSize = datasize;
                    f.name = name;
                    f.fullName = fullname;

                    fileEntries.put(fileid, f);
                }
            }
        }
    }

    @Override
    public void close() throws IOException
    {
        file.close();
    }


    @Override
    public boolean directoryExists(String directory)
    {
        for (DirEntry de : dirEntries.values())
        {
            if (de.fullName.equalsIgnoreCase(directory))
                return true;
        }
        
        return false;
    }

    @Override
    public List<String> getDirectories(String directory)
    {
        DirEntry dir = null;
        for (DirEntry de : dirEntries.values())
        {
            if (de.fullName.equalsIgnoreCase(directory))
            {
                dir = de;
                break;
            }
        }
        
        if (dir == null) return null;
        
        List<String> ret = new ArrayList<>();
        for (DirEntry de : dirEntries.values())
        {
            if (de.parentDir == dir.ID)
                ret.add(de.name);
        }
        
        return ret;
    }


    @Override
    public boolean fileExists(String filename)
    {
        for (FileEntry fe : fileEntries.values())
        {
            if (fe.fullName.equalsIgnoreCase(filename))
                return true;
        }
        
        return false;
    }

    @Override
    public List<String> getFiles(String directory)
    {
        DirEntry dir = null;
        for (DirEntry de : dirEntries.values())
        {
            if (de.fullName.equalsIgnoreCase(directory))
            {
                dir = de;
                break;
            }
        }
        
        if (dir == null) return null;
        
        List<String> ret = new ArrayList<>();
        for (FileEntry fe : fileEntries.values())
        {
            if (fe.parentDir == dir.ID)
                ret.add(fe.name);
        }
        
        return ret;
    }

    @Override
    public FileBase openFile(String filename) throws FileNotFoundException
    {
        for (FileEntry fe : fileEntries.values())
        {
            if (fe.fullName.equalsIgnoreCase(filename))
            {
                try
                {
                    return new RarcFile(this, fe.ID);
                }
                catch (IOException ex)
                {
                    throw new FileNotFoundException("got IOException");
                }
            }
        }
        
        throw new FileNotFoundException(filename + " not found in RARC");
    }


    // support functions for RarcFile
    public byte[] getFileContents(int id) throws IOException
    {
        FileEntry fe = fileEntries.get(id);

        file.position(fileDataOffset + fe.dataOffset);
        return file.readBytes((int)fe.dataSize);
    }

    public void reinsertFile(RarcFile _file) throws IOException
    {
        FileEntry fe = fileEntries.get(_file.ID());

        int fileoffset = fileDataOffset + fe.dataOffset;
        int oldlength = (int)fe.dataSize;
        int newlength = (int)_file.getLength();
        int delta = newlength - oldlength;

        if (newlength != oldlength)
        {
            file.position(fileoffset + oldlength);
            byte[] tomove = file.readBytes((int)(file.getLength() - file.position()));

            file.position(fileoffset + newlength);
            file.setLength(file.getLength() + delta);
            file.writeBytes(tomove);

            fe.dataSize = (int)newlength;
            file.position(fe.entryOffset + 0xC);
            file.writeInt(fe.dataSize);

            for (FileEntry tofix : fileEntries.values())
            {
                if (tofix.ID == fe.ID) continue;
                if (tofix.dataOffset < (fe.dataOffset + oldlength)) continue;

                tofix.dataOffset = (int)(tofix.dataOffset + delta);
                file.position(tofix.entryOffset + 0x8);
                file.writeInt(tofix.dataOffset);
            }
        }

        file.position(fileoffset);
        _file.position(0);
        byte[] data = _file.readBytes(newlength);
        file.writeBytes(data);

        file.save();
    }


    private class FileEntry
    {
        public int entryOffset;

        public int ID;
        public int nameOffset;
        public int dataOffset;
        public int dataSize;

        public int parentDir;

        public String name;
        public String fullName;
    }

    private class DirEntry
    {
        public int entryOffset;

        public int ID;
        public int nameOffset;

        public int parentDir;

        public String name;
        public String fullName;
    }


    private FileBase file;

    private int fileDataOffset;
    private int numDirNodes;
    private int dirNodesOffset;
    private int fileEntriesOffset;
    private int stringTableOffset;

    private LinkedHashMap<Integer, FileEntry> fileEntries;
    private LinkedHashMap<Integer, DirEntry> dirEntries;
}
