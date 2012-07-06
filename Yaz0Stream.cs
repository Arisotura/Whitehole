using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Whitehole
{
    public class Yaz0Stream : MemoryStream
    {
        public Yaz0Stream(Stream backend)
            : base(1)
        {
            if (backend is Yaz0Stream) throw new Exception("sorry but no");

            m_Backend = backend;

            m_Backend.Position = 0;
            byte[] buffer = new byte[m_Backend.Length];
            m_Backend.Read(buffer, 0, (int)m_Backend.Length);

            Yaz0.Decompress(ref buffer);
            Position = 0;
            Write(buffer, 0, buffer.Length);
        }

        public void Flush(bool recompress)
        {
            byte[] buffer = new byte[Length];
            Position = 0;
            Read(buffer, 0, (int)Length);
            if (recompress) Yaz0.Compress(ref buffer);

            m_Backend.Position = 0;
            m_Backend.SetLength(buffer.Length);
            m_Backend.Write(buffer, 0, buffer.Length);
            m_Backend.Flush();
        }

        public override void Flush()
        {
            Flush(false);
        }

        public override void Close()
        {
            m_Backend.Close();
            base.Close();
        }


        private Stream m_Backend;
    }
}
