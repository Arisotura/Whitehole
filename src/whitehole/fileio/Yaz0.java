package whitehole.fileio;

public class Yaz0 
{
    // inspired from http://www.amnoid.de/gc/yaz0.txt
    public static byte[] Decompress(byte[] data)
    {
        if (data[0] != 'Y' || data[1] != 'a' || data[2] != 'z' || data[3] != '0')
            return data;

        int fullsize = ((data[4] & 0xFF) << 24) | ((data[5] & 0xFF) << 16) | ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);
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

                    int dist = ((b1 & 0xF) << 8) | (b2 & 0xFF);
                    int copysrc = outpos - (dist + 1);

                    int nbytes = (b1 & 0xFF) >>> 4;
                    if (nbytes == 0) nbytes = (data[inpos++] & 0xFF) + 0x12;
                    else nbytes += 2;

                    for (int j = 0; j < nbytes; j++)
                        output[outpos++] = output[copysrc++];
                }

                block <<= 1;
                if (outpos >= fullsize || inpos >= data.length)
                    break;
            }
        }

        return output;
    }
}
