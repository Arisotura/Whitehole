using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

// inspired from http://www.azillionmonkeys.com/qed/hash.html

namespace Whitehole
{
    public static class SuperFastHash
    {
        public static uint Calculate(byte[] data, uint start)
        {
            int len = data.Length;
            uint hash = start, tmp;
            int rem;

            if (len < 1) return 0;

            rem = data.Length & 0x3;
            len >>= 2;

            int pos = 0;
            for (; len > 0; len--)
            {
                hash += (uint)(data[pos++] | (data[pos++] << 8));
                tmp = (uint)(((data[pos++] | (data[pos++] << 8)) << 11) ^ hash);
                hash = ((hash << 16) ^ tmp);
                hash += (hash >> 11);
            }

            switch (rem)
            {
                case 3:
                    hash += (uint)(data[pos++] | (data[pos++] << 8));
                    hash ^= (hash << 16);
                    hash ^= (uint)(data[pos++] << 18);
                    hash += (hash >> 11);
                    break;

                case 2:
                    hash += (uint)(data[pos++] | (data[pos++] << 8));
                    hash ^= (hash << 11);
                    hash += (hash >> 17);
                    break;

                case 1:
                    hash += (uint)data[pos++];
                    hash ^= (hash << 10);
                    hash += (hash >> 1);
                    break;
            }

            hash ^= (hash << 3);
            hash += (hash >> 5);
            hash ^= (hash << 4);
            hash += (hash >> 17);
            hash ^= (hash << 25);
            hash += (hash >> 6);

            return hash;
        }
    }
}
