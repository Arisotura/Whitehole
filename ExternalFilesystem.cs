using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Whitehole
{
    public class ExternalFilesystem : FilesystemBase
    {
        public ExternalFilesystem(string basedir)
        {
            if (basedir.EndsWith("/") || basedir.EndsWith("\\"))
                basedir = basedir.Substring(0, basedir.Length - 1);

            m_BaseDirectory = basedir;
            if (!Directory.Exists(basedir)) throw new Exception("Directory '" + basedir + "' doesn't exist");
        }


        public override string[] GetDirectories(string directory)
        {
            string[] ret = Directory.GetDirectories(m_BaseDirectory + directory);

            for (int i = 0; i < ret.Length; i++)
            {
                ret[i] = ret[i].Substring(ret[i].LastIndexOfAny(new char[] { '/', '\\' }) + 1);
            }

            return ret;
        }

        public override bool DirectoryExists(string directory)
        {
            return Directory.Exists(m_BaseDirectory + directory);
        }


        public override string[] GetFiles(string directory)
        {
            string[] ret = Directory.GetFiles(m_BaseDirectory + directory);

            for (int i = 0; i < ret.Length; i++)
            {
                ret[i] = ret[i].Substring(ret[i].LastIndexOfAny(new char[] { '/', '\\' }) + 1);
            }

            return ret;
        }

        public override bool FileExists(string filename)
        {
            return File.Exists(m_BaseDirectory + filename);
        }

        public override FileBase OpenFile(string filename)
        {
            return new ExternalFile(m_BaseDirectory + filename, false);
        }


        private string m_BaseDirectory;
    }


    public class ExternalFile : FileBase
    {
        public ExternalFile(string filename, bool create)
        {
            Stream = File.Open(filename, create ? FileMode.Create : FileMode.Open, FileAccess.ReadWrite, FileShare.Read);
        }
    }
}
