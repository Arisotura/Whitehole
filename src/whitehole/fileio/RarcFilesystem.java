package whitehole.fileio;

import java.io.*;
import java.util.*;

public class RarcFilesystem implements FilesystemBase
{
    public RarcFilesystem(FileBase file) throws IOException
    {
        m_File = new Yaz0File(file);
        m_File.SetBigEndian(true);

        m_File.Position(0);
        int tag = m_File.ReadInt();
        if (tag != 0x52415243) 
            throw new IOException(String.format("File isn't a RARC (tag 0x%1$X8, expected 0x52415243)", tag));

        m_File.Position(0xC);
        m_FileDataOffset = m_File.ReadInt() + 0x20;
        m_File.Position(0x20);
        m_NumDirNodes = m_File.ReadInt();
        m_DirNodesOffset = m_File.ReadInt() + 0x20;
        m_File.Skip(0x4);
        m_FileEntriesOffset = m_File.ReadInt() + 0x20;
        m_File.Skip(0x4);
        m_StringTableOffset = m_File.ReadInt() + 0x20;

        m_DirEntries = new HashMap<>();
        m_FileEntries = new HashMap<>();

        DirEntry root = new DirEntry();
        root.ID = 0;
        root.ParentDir = 0xFFFFFFFF;

        m_File.Position(m_DirNodesOffset + 0x6);
        int rnoffset = m_File.ReadShort();
        m_File.Position(m_StringTableOffset + rnoffset);
        root.Name = m_File.ReadString("ASCII", 0);
        root.FullName = "/" + root.Name;

        m_DirEntries.put(0, root);

        for (int i = 0; i < m_NumDirNodes; i++)
        {
            DirEntry parentdir = m_DirEntries.get(i);

            m_File.Position(m_DirNodesOffset + (i * 0x10) + 10);

            short numentries = m_File.ReadShort();
            int firstentry = m_File.ReadInt();

            for (int j = 0; j < numentries; j++)
            {
                int entryoffset = m_FileEntriesOffset + ((j + firstentry) * 0x14);
                m_File.Position(entryoffset);

                int fileid = m_File.ReadShort();
                m_File.Skip(4);
                int nameoffset = m_File.ReadShort();
                int dataoffset = m_File.ReadInt();
                int datasize = m_File.ReadInt();

                m_File.Position(m_StringTableOffset + nameoffset);
                String name = m_File.ReadString("ASCII", 0);
                if (name.equals(".") || name.equals("..")) continue;

                String fullname = parentdir.FullName + "/" + name;

                if (fileid == 0xFFFF)
                {
                    DirEntry d = new DirEntry();
                    d.EntryOffset = entryoffset;
                    d.ID = dataoffset;
                    d.ParentDir = i;
                    d.NameOffset = nameoffset;
                    d.Name = name;
                    d.FullName = fullname;

                    m_DirEntries.put(dataoffset, d);
                }
                else
                {
                    FileEntry f = new FileEntry();
                    f.EntryOffset = entryoffset;
                    f.ID = fileid;
                    f.ParentDir = i;
                    f.NameOffset = nameoffset;
                    f.DataOffset = dataoffset;
                    f.DataSize = datasize;
                    f.Name = name;
                    f.FullName = fullname;

                    m_FileEntries.put(fileid, f);
                }
            }
        }
    }

    @Override
    public void Close() throws IOException
    {
        m_File.Close();
    }


    @Override
    public Boolean DirectoryExists(String directory)
    {
        for (DirEntry de : m_DirEntries.values())
        {
            if (de.FullName.equalsIgnoreCase(directory))
                return true;
        }
        
        return false;
    }

    @Override
    public String[] GetDirectories(String directory)
    {
        DirEntry dir = null;
        for (DirEntry de : m_DirEntries.values())
        {
            if (de.FullName.equalsIgnoreCase(directory))
            {
                dir = de;
                break;
            }
        }
        
        if (dir == null) return null;
        
        List<String> ret = new ArrayList<>();
        for (DirEntry de : m_DirEntries.values())
        {
            if (de.ParentDir == dir.ID)
                ret.add(de.Name);
        }
        
        return Arrays.copyOf(ret.toArray(), ret.size(), String[].class);
    }


    @Override
    public Boolean FileExists(String filename)
    {
        for (FileEntry fe : m_FileEntries.values())
        {
            if (fe.FullName.equalsIgnoreCase(filename))
                return true;
        }
        
        return false;
    }

    @Override
    public String[] GetFiles(String directory)
    {
        DirEntry dir = null;
        for (DirEntry de : m_DirEntries.values())
        {
            if (de.FullName.equalsIgnoreCase(directory))
            {
                dir = de;
                break;
            }
        }
        
        if (dir == null) return null;
        
        List<String> ret = new ArrayList<>();
        for (FileEntry fe : m_FileEntries.values())
        {
            if (fe.ParentDir == dir.ID)
                ret.add(fe.Name);
        }
        
        return Arrays.copyOf(ret.toArray(), ret.size(), String[].class);
    }

    @Override
    public FileBase OpenFile(String filename) throws FileNotFoundException
    {
        for (FileEntry fe : m_FileEntries.values())
        {
            if (fe.FullName.equalsIgnoreCase(filename))
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
    public byte[] GetFileContents(int id) throws IOException
    {
        FileEntry fe = m_FileEntries.get(id);

        m_File.Position(m_FileDataOffset + fe.DataOffset);
        return m_File.ReadBytes((int)fe.DataSize);
    }

    public void ReinsertFile(RarcFile file) throws IOException
    {
        FileEntry fe = m_FileEntries.get(file.ID());

        int fileoffset = m_FileDataOffset + fe.DataOffset;
        int oldlength = (int)fe.DataSize;
        int newlength = (int)file.GetLength();
        int delta = newlength - oldlength;

        if (newlength != oldlength)
        {
            m_File.Position(fileoffset + oldlength);
            byte[] tomove = m_File.ReadBytes((int)(m_File.GetLength() - m_File.Position()));

            m_File.Position(fileoffset + newlength);
            m_File.SetLength(m_File.GetLength() + delta);
            m_File.WriteBytes(tomove);

            fe.DataSize = (int)newlength;
            m_File.Position(fe.EntryOffset + 0xC);
            m_File.WriteInt(fe.DataSize);

            for (FileEntry tofix : m_FileEntries.values())
            {
                if (tofix.ID == fe.ID) continue;
                if (tofix.DataOffset < (fe.DataOffset + oldlength)) continue;

                tofix.DataOffset = (int)(tofix.DataOffset + delta);
                m_File.Position(tofix.EntryOffset + 0x8);
                m_File.WriteInt(tofix.DataOffset);
            }
        }

        m_File.Position(fileoffset);
        file.Position(0);
        byte[] data = file.ReadBytes(newlength);
        m_File.WriteBytes(data);

        m_File.Save();
    }


    private class FileEntry
    {
        public int EntryOffset;

        public int ID;
        public int NameOffset;
        public int DataOffset;
        public int DataSize;

        public int ParentDir;

        public String Name;
        public String FullName;
    }

    private class DirEntry
    {
        public int EntryOffset;

        public int ID;
        public int NameOffset;

        public int ParentDir;

        public String Name;
        public String FullName;
    }


    private FileBase m_File;

    private int m_FileDataOffset;
    private int m_NumDirNodes;
    private int m_DirNodesOffset;
    private int m_FileEntriesOffset;
    private int m_StringTableOffset;

    private HashMap<Integer, FileEntry> m_FileEntries;
    private HashMap<Integer, DirEntry> m_DirEntries;
}
