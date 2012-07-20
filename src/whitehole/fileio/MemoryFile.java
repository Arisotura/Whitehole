package whitehole.fileio;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

public class MemoryFile implements FileBase
{
    public MemoryFile(byte[] buffer) throws IOException
    {
        m_Buffer = buffer;
        m_Position = 0;
        m_BigEndian = false;
    }
    
    
    @Override
    public void Save() throws IOException
    {
        // nothing to do, saving will be handled by derivates
    }

    @Override
    public void Close() throws IOException
    {
        // nothing to do, lol
    }
    
    
    @Override
    public void ReleaseStorage()
    {
        m_Buffer = null;
    }
    
    
    @Override
    public void SetBigEndian(Boolean bigendian)
    {
        m_BigEndian = bigendian;
    }
    
    
    @Override
    public long GetLength() throws IOException
    {
        return (long)m_Buffer.length;
    }
    
    @Override
    public void SetLength(long length) throws IOException
    {
        if (length >= 0x80000000L) throw new IOException("hey calm down, you're gonna eat all the RAM");
        ResizeBuffer((int)length);
    }
    

    @Override
    public long Position() throws IOException
    {
        return (long)m_Position;
    }

    @Override
    public void Position(long newpos) throws IOException
    {
        if (newpos >= 0x80000000L) throw new IOException("hey calm down, you're gonna eat all the RAM");
        m_Position = (int)newpos;
    }
    
    @Override
    public void Skip(long nbytes) throws IOException
    {
        m_Position += nbytes;
        if (m_Position >= 0x80000000L) throw new IOException("hey calm down, you're gonna eat all the RAM");
    }
    

    @Override
    public byte ReadByte() throws IOException
    {
        if (m_Position + 1 > m_Buffer.length) return 0;
        return m_Buffer[m_Position++];
    }

    @Override
    public short ReadShort() throws IOException
    {
        if (m_Position + 2 > m_Buffer.length) return 0;
        if (m_BigEndian)
            return (short)(((m_Buffer[m_Position++] & 0xFF) << 8) | 
                    (m_Buffer[m_Position++] & 0xFF));
        else
            return (short)((m_Buffer[m_Position++] & 0xFF) | 
                    ((m_Buffer[m_Position++] & 0xFF) << 8));
    }

    @Override
    public int ReadInt() throws IOException
    {
        if (m_Position + 4 > m_Buffer.length) return 0;
        if (m_BigEndian)
            return (int)(((m_Buffer[m_Position++] & 0xFF) << 24) | 
                    ((m_Buffer[m_Position++] & 0xFF) << 16) | 
                    ((m_Buffer[m_Position++] & 0xFF) << 8) | 
                    (m_Buffer[m_Position++] & 0xFF));
        else
            return (int)((m_Buffer[m_Position++] & 0xFF) | 
                    ((m_Buffer[m_Position++] & 0xFF) << 8) | 
                    ((m_Buffer[m_Position++] & 0xFF) << 16) | 
                    ((m_Buffer[m_Position++] & 0xFF) << 24));
    }

    @Override
    public float ReadFloat() throws IOException
    {
        return Float.intBitsToFloat(ReadInt());
    }

    @Override
    public String ReadString(String encoding, int length) throws IOException
    {
        Charset charset = Charset.forName(encoding);
        CharsetDecoder dec = charset.newDecoder();
        ByteBuffer bin = ByteBuffer.wrap(m_Buffer);
        CharBuffer bout = CharBuffer.allocate(1);
        String ret = "";
        
        bin.position(m_Position);
        for (int i = 0; length == 0 || i < length; i++)
        {
            CoderResult res = dec.decode(bin, bout, false);
            if (res == CoderResult.UNDERFLOW)
                break;
            else if (res != CoderResult.OVERFLOW)
                throw new IOException("Error while reading string");
            
            char ch = bout.get(0);
            if (ch == '\0') break;
            
            bout.clear();
            ret += ch;
        }
        
        return ret;
    }
    
    @Override
    public byte[] ReadBytes(int length) throws IOException
    {
        byte[] ret = new byte[length];
        for (int i = 0; i < length; i++)
            ret[i] = m_Buffer[m_Position++];
        return ret;
    }
    

    @Override
    public void WriteByte(byte val) throws IOException
    {
        AutoExpand(m_Position + 1);
        m_Buffer[m_Position++] = val;
    }

    @Override
    public void WriteShort(short val) throws IOException
    {
        AutoExpand(m_Position + 2);
        if (m_BigEndian)
        {
            m_Buffer[m_Position++] = (byte)((val >>> 8) & 0xFF);
            m_Buffer[m_Position++] = (byte)(val & 0xFF);
        }
        else
        {
            m_Buffer[m_Position++] = (byte)(val & 0xFF);
            m_Buffer[m_Position++] = (byte)((val >>> 8) & 0xFF);
        }
    }

    @Override
    public void WriteInt(int val) throws IOException
    {
        AutoExpand(m_Position + 4);
        if (m_BigEndian)
        {
            m_Buffer[m_Position++] = (byte)((val >>> 24) & 0xFF);
            m_Buffer[m_Position++] = (byte)((val >>> 16) & 0xFF);
            m_Buffer[m_Position++] = (byte)((val >>> 8) & 0xFF);
            m_Buffer[m_Position++] = (byte)(val & 0xFF);
        }
        else
        {
            m_Buffer[m_Position++] = (byte)(val & 0xFF);
            m_Buffer[m_Position++] = (byte)((val >>> 8) & 0xFF);
            m_Buffer[m_Position++] = (byte)((val >>> 16) & 0xFF);
            m_Buffer[m_Position++] = (byte)((val >>> 24) & 0xFF);
        }
    }

    @Override
    public void WriteFloat(float val) throws IOException
    {
        WriteInt(Float.floatToIntBits(val));
    }

    @Override
    public int WriteString(String encoding, String val, int length) throws IOException
    {
        Charset charset = Charset.forName(encoding);
        CharsetEncoder enc = charset.newEncoder();
        CharBuffer bin = CharBuffer.allocate(1);
        ByteBuffer bout = ByteBuffer.allocate(8);
        int len = 0;
        
        for (int i = 0; i < ((length > 0) ? length : val.length()); i++)
        {
            bin.put(val.charAt(i));
            bin.rewind();
            CoderResult res = enc.encode(bin, bout, false);
            if (res != CoderResult.UNDERFLOW)
                throw new IOException("Error while writing string");
            
            int bytesize = bout.position();
            len += bytesize;
            AutoExpand(m_Position + bytesize);
            for (int j = 0; j < bytesize; j++)
                m_Buffer[m_Position++] = bout.get(j);
            
            bin.clear();
            bout.clear();
        }
        
        if (length == 0 || val.length() < length)
        {
            bin.put('\0');
            bin.rewind();
            CoderResult res = enc.encode(bin, bout, false);
            if (res != CoderResult.UNDERFLOW)
                throw new IOException("Error while writing string");
            
            int bytesize = bout.position();
            len += bytesize;
            AutoExpand(m_Position + bytesize);
            for (int j = 0; j < bytesize; j++)
                m_Buffer[m_Position++] = bout.get(j);
        }
        
        return len;
    }
    
    @Override
    public void WriteBytes(byte[] stuff) throws IOException
    {
        AutoExpand(m_Position + stuff.length);
        for (byte b : stuff)
            m_Buffer[m_Position++] = b;
    }
    
    
    @Override
    public byte[] GetContents() throws IOException
    {
        return m_Buffer;
    }
    
    @Override
    public void SetContents(byte[] buf) throws IOException
    {
        m_Buffer = buf;
    }
    
    
    protected byte[] m_Buffer;
    private int m_Position;
    private Boolean m_BigEndian;
    
    private void ResizeBuffer(int newsize)
    {
        byte[] newbuf = new byte[newsize];
        if (newsize > 0 && m_Buffer.length > 0)
            System.arraycopy(m_Buffer, 0, newbuf, 0, Math.min(newsize, m_Buffer.length));
        m_Buffer = newbuf;
    }
    
    private void AutoExpand(int newend)
    {
        if (newend <= m_Buffer.length) return;
        ResizeBuffer(newend);
    }
}
