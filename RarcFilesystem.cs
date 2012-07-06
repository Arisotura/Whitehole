using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Whitehole
{
    public class RarcFilesystem : FilesystemBase
    {
        public RarcFilesystem(FileBase file)
        {
            m_File = file;
            m_File.Stream = new Yaz0Stream(m_File.Stream);
            m_File.BigEndian = true;

            m_File.Stream.Position = 0;
            uint tag = m_File.Reader.ReadUInt32();
            if (tag != 0x52415243) throw new Exception("File isn't a RARC (tag 0x" + tag.ToString("X8") + ", expected 0x52415243)");

            m_File.Stream.Position = 0xC;
            m_FileDataOffset = m_File.Reader.ReadUInt32() + 0x20;
            m_File.Stream.Position = 0x20;
            m_NumDirNodes = m_File.Reader.ReadUInt32();
            m_DirNodesOffset = m_File.Reader.ReadUInt32() + 0x20;
            m_File.Stream.Position += 0x4;
            m_FileEntriesOffset = m_File.Reader.ReadUInt32() + 0x20;
            m_File.Stream.Position += 0x4;
            m_StringTableOffset = m_File.Reader.ReadUInt32() + 0x20;

            m_DirEntries = new Dictionary<uint, DirEntry>();
            m_FileEntries = new Dictionary<uint, FileEntry>();

            DirEntry root = new DirEntry();
            root.ID = 0;
            root.ParentDir = 0xFFFFFFFF;

            m_File.Stream.Position = m_DirNodesOffset + 0x6;
            uint rnoffset = m_File.Reader.ReadUInt16();
            m_File.Stream.Position = m_StringTableOffset + rnoffset;
            root.Name = m_File.ReadString();
            root.FullName = "/" + root.Name;

            m_DirEntries.Add(0, root);

            for (uint i = 0; i < m_NumDirNodes; i++)
            {
                DirEntry parentdir = m_DirEntries[i];

                m_File.Stream.Position = m_DirNodesOffset + (i * 0x10) + 10;

                ushort numentries = m_File.Reader.ReadUInt16();
                uint firstentry = m_File.Reader.ReadUInt32();

                for (uint j = 0; j < numentries; j++)
                {
                    uint entryoffset = m_FileEntriesOffset + ((j + firstentry) * 0x14);
                    m_File.Stream.Position = entryoffset;

                    uint fileid = m_File.Reader.ReadUInt16();
                    m_File.Stream.Position += 4;
                    uint nameoffset = m_File.Reader.ReadUInt16();
                    uint dataoffset = m_File.Reader.ReadUInt32();
                    uint datasize = m_File.Reader.ReadUInt32();

                    m_File.Stream.Position = m_StringTableOffset + nameoffset;
                    string name = m_File.ReadString();
                    if (name == "." || name == "..") continue;

                    string fullname = parentdir.FullName + "/" + name;

                    if (fileid == 0xFFFF)
                    {
                        DirEntry d = new DirEntry();
                        d.EntryOffset = entryoffset;
                        d.ID = dataoffset;
                        d.ParentDir = i;
                        d.NameOffset = nameoffset;
                        d.Name = name;
                        d.FullName = fullname;

                        m_DirEntries.Add(dataoffset, d);
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

                        m_FileEntries.Add(fileid, f);
                    }
                }
            }
        }

        public override void Close()
        {
            m_File.Close();
        }


        public override bool DirectoryExists(string directory)
        {
            DirEntry dir = m_DirEntries.Values.FirstOrDefault(de => de.FullName.ToLower() == directory.ToLower());
            return dir != null;
        }

        public override string[] GetDirectories(string directory)
        {
            DirEntry dir = m_DirEntries.Values.FirstOrDefault(de => de.FullName.ToLower() == directory.ToLower());
            if (dir == null) return null;
            IEnumerable<DirEntry> subdirs = m_DirEntries.Values.Where(de => de.ParentDir == dir.ID);

            List<string> ret = new List<string>();
            foreach (DirEntry de in subdirs) ret.Add(de.Name);
            return ret.ToArray();
        }


        public override bool FileExists(string filename)
        {
            FileEntry file = m_FileEntries.Values.FirstOrDefault(fe => fe.FullName.ToLower() == filename.ToLower());
            return file != null;
        }

        public override string[] GetFiles(string directory)
        {
            DirEntry dir = m_DirEntries.Values.FirstOrDefault(de => de.FullName.ToLower() == directory.ToLower());
            if (dir == null) return null;
            IEnumerable<FileEntry> files = m_FileEntries.Values.Where(fe => fe.ParentDir == dir.ID);

            List<string> ret = new List<string>();
            foreach (FileEntry fe in files) ret.Add(fe.Name);
            return ret.ToArray();
        }

        public override FileBase OpenFile(string filename)
        {
            FileEntry file = m_FileEntries.Values.FirstOrDefault(fe => fe.FullName.ToLower() == filename.ToLower());
            if (file == null) return null;

            return new RarcFile(this, file.ID);
        }


        // support functions for RarcFile
        public byte[] GetFileContents(RarcFile file)
        {
            FileEntry fe = m_FileEntries[file.ID];

            m_File.Stream.Position = m_FileDataOffset + fe.DataOffset;
            return m_File.Reader.ReadBytes((int)fe.DataSize);
        }

        public void ReinsertFile(RarcFile file)
        {
            FileEntry fe = m_FileEntries[file.ID];

            uint fileoffset = m_FileDataOffset + fe.DataOffset;
            int oldlength = (int)fe.DataSize;
            int newlength = (int)file.Stream.Length;
            int delta = newlength - oldlength;

            if (newlength != oldlength)
            {
                m_File.Stream.Position = fileoffset + oldlength;
                byte[] tomove = m_File.Reader.ReadBytes((int)(m_File.Stream.Length - m_File.Stream.Position));

                m_File.Stream.Position = fileoffset + newlength;
                m_File.Stream.SetLength(m_File.Stream.Length + delta);
                m_File.Writer.Write(tomove);

                fe.DataSize = (uint)newlength;
                m_File.Stream.Position = fe.EntryOffset + 0xC;
                m_File.Writer.Write(fe.DataSize);

                foreach (FileEntry tofix in m_FileEntries.Values)
                {
                    if (tofix.ID == fe.ID) continue;
                    if (tofix.DataOffset < (fe.DataOffset + oldlength)) continue;

                    tofix.DataOffset = (uint)(tofix.DataOffset + delta);
                    m_File.Stream.Position = tofix.EntryOffset + 0x8;
                    m_File.Writer.Write(tofix.DataOffset);
                }
            }

            m_File.Stream.Position = fileoffset;
            file.Stream.Position = 0;
            byte[] data = file.Reader.ReadBytes(newlength);
            m_File.Writer.Write(data);

            m_File.Flush();
        }


        private class FileEntry
        {
            public uint EntryOffset;

            public uint ID;
            public uint NameOffset;
            public uint DataOffset;
            public uint DataSize;

            public uint ParentDir;

            public string Name;
            public string FullName;
        }

        private class DirEntry
        {
            public uint EntryOffset;

            public uint ID;
            public uint NameOffset;

            public uint ParentDir;

            public string Name;
            public string FullName;
        }


        private FileBase m_File;

        private uint m_FileDataOffset;
        private uint m_NumDirNodes;
        private uint m_DirNodesOffset;
        private uint m_FileEntriesOffset;
        private uint m_StringTableOffset;

        private Dictionary<uint, FileEntry> m_FileEntries;
        private Dictionary<uint, DirEntry> m_DirEntries;
    }


    public class RarcFile : FileBase
    {
        public RarcFile(RarcFilesystem fs, uint id)
        {
            m_FS = fs;
            m_ID = id;

            byte[] buffer = m_FS.GetFileContents(this);
            Stream = new MemoryStream(buffer.Length);
            Writer.Write(buffer);
        }

        public override void Flush()
        {
            Stream.Flush();
            m_FS.ReinsertFile(this);
        }


        private RarcFilesystem m_FS;
        private uint m_ID;

        public uint ID { get { return m_ID; } }
    }
}
