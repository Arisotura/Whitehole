using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Whitehole
{
    public static class Yaz0
    {
        // TODO: put compression in use?
        // note: compression is slow when dealing with large files (eg. 3D models)
        // it should be made optional, and show a progress dialog and all

        private static void FindOccurence(byte[] data, int pos, ref int offset, ref int length)
        {
            offset = -1;
            length = 0;

            if (pos >= data.Length - 2) return;

            Dictionary<int, int> occurences = new Dictionary<int, int>();

            int len = 0;
            int start = (pos > 4096) ? pos - 4096 : 0;
            for (int i = start; i < pos; i++)
            {
                if (i >= data.Length - 2) break;

                if (data[i] != data[pos] || data[i + 1] != data[pos + 1] || data[i + 2] != data[pos + 2])
                    continue;

                len = 3;
                while ((i + len < data.Length) && (pos + len < data.Length) && (data[i + len] == data[pos + len]))
                    len++;

                occurences.Add(i, len);
            }

            foreach (KeyValuePair<int, int> occ in occurences)
            {
                if (occ.Value > length)
                {
                    offset = occ.Key;
                    length = occ.Value;
                }
            }
        }

        public static void Compress(ref byte[] data)
        {
            if (data[0] == 'Y' && data[1] == 'a' && data[2] == 'z' && data[3] == '0')
                return;

            byte[] output = new byte[16 + data.Length + (data.Length / 8)];

            output[0] = (byte)'Y';
            output[1] = (byte)'a';
            output[2] = (byte)'z';
            output[3] = (byte)'0';

            uint fullsize = (uint)data.Length;
            output[4] = (byte)(fullsize >> 24);
            output[5] = (byte)(fullsize >> 16);
            output[6] = (byte)(fullsize >> 8);
            output[7] = (byte)fullsize;

            int inpos = 0, outpos = 16;
            int occ_offset = -1, occ_length = 0;

            while (inpos < fullsize)
            {
                int datastart = outpos + 1;
                byte block = 0;

                for (int i = 0; i < 8; i++)
                {
                    block <<= 1;

                    if (inpos < data.Length)
                    {
                        if (occ_offset == -2)
                            FindOccurence(data, inpos, ref occ_offset, ref occ_length);

                        int next_offset = -1, next_length = 0;
                        FindOccurence(data, inpos + 1, ref next_offset, ref next_length);
                        if (next_length > occ_length + 1) occ_offset = -1;

                        if (occ_offset != -1)
                        {
                            int disp = inpos - occ_offset - 1;
                            if (disp > 4095) throw new Exception("DISP OUT OF RANGE!");

                            if (occ_length > 17)
                            {
                                if (occ_length > 273) occ_length = 273;

                                output[datastart++] = (byte)(disp >> 8);
                                output[datastart++] = (byte)disp;
                                output[datastart++] = (byte)(occ_length - 18);
                            }
                            else
                            {
                                output[datastart++] = (byte)(((occ_length - 2) << 4) | (disp >> 8));
                                output[datastart++] = (byte)disp;
                            }

                            inpos += occ_length;
                            occ_offset = -2;
                        }
                        else
                        {
                            output[datastart++] = data[inpos++];
                            block |= 0x01;
                        }

                        if (occ_offset != -2)
                        {
                            occ_offset = next_offset;
                            occ_length = next_length;
                        }
                    }
                }

                output[outpos] = block;
                outpos = datastart;
            }

            Array.Resize(ref data, outpos);
            Array.Resize(ref output, outpos);
            output.CopyTo(data, 0);
        }

        // inspired from http://www.amnoid.de/gc/yaz0.txt
        public static void Decompress(ref byte[] data)
        {
            if (data[0] != 'Y' || data[1] != 'a' || data[2] != 'z' || data[3] != '0')
                return;

            int fullsize = (data[4] << 24) | (data[5] << 16) | (data[6] << 8) | data[7];
            byte[] output = new byte[fullsize];

            int inpos = 16, outpos = 0;
            while (outpos < fullsize)
            {
                byte block  = data[inpos++];

                for (int i = 0; i < 8; i++)
                {
                    if ((block & 0x80) != 0)
                    {
                        // copy one plain byte
                        output[outpos++] = data[inpos++];
                    }
                    else
                    {
                        // copy N compressed bytes
                        byte b1 = data[inpos++];
                        byte b2 = data[inpos++];

                        int dist = ((b1 & 0xF) << 8) | b2;
                        int copysrc = outpos - (dist + 1);

                        int nbytes = b1 >> 4;
                        if (nbytes == 0) nbytes = data[inpos++] + 0x12;
                        else nbytes += 2;

                        for (int j = 0; j < nbytes; j++)
                            output[outpos++] = output[copysrc++];
                    }

                    block <<= 1;
                    if (outpos >= fullsize || inpos >= data.Length)
                        break;
                }
            }

            Array.Resize(ref data, fullsize);
            output.CopyTo(data, 0);
        }
    }
}
