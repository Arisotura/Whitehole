using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Whitehole
{
    public class FilesystemBase
    {
        public virtual void Close() { }

        public virtual string[] GetDirectories(string directory)
        { throw new NotImplementedException("FilesystemBase.GetDirectories()"); }

        public virtual bool DirectoryExists(string directory)
        { throw new NotImplementedException("FilesystemBase.DirectoryExists()"); }


        public virtual string[] GetFiles(string directory)
        { throw new NotImplementedException("FilesystemBase.GetFiles()"); }

        public virtual bool FileExists(string filename)
        { throw new NotImplementedException("FilesystemBase.FileExists()"); }

        public virtual FileBase OpenFile(string filename)
        { throw new NotImplementedException("FilesystemBase.OpenFile()"); }
    }

    public class FileBase
    {
        public Stream Stream
        {
            get { return m_Stream; }
            set
            {
                m_Stream = value;
                InitRW();
            }
        }

        public bool BigEndian
        {
            get { return m_BigEndian; }
            set
            {
                m_BigEndian = value;
                InitRW();
            }
        }

        public Encoding Encoding
        {
            get { return m_Encoding; }
            set
            {
                m_Encoding = value;
                InitRW();
            }
        }

        public BinaryReader Reader;
        public BinaryWriter Writer;

        private Stream m_Stream;
        private bool m_BigEndian;
        private Encoding m_Encoding = Encoding.ASCII;

        private void InitRW()
        {
            Reader = m_BigEndian ? new BinaryReaderBE(m_Stream, m_Encoding) : new BinaryReader(m_Stream, m_Encoding);
            Writer = m_BigEndian ? new BinaryWriterBE(m_Stream, m_Encoding) : new BinaryWriter(m_Stream, m_Encoding);
        }


        public string ReadString()
        {
            string ret = "";
            char c;
            while ((c = Reader.ReadChar()) != '\0')
                ret += c;
            return ret;
        }

        public int WriteString(string str)
        {
            int oldpos = (int)Stream.Position;

            foreach (char c in str)
                Writer.Write(c);
            Writer.Write('\0');

            return (int)(Stream.Position - oldpos);
        }


        public virtual void Flush()
        {
            m_Stream.Flush();
        }

        public virtual void Close()
        {
            m_Stream.Close();
        }
    }
}
