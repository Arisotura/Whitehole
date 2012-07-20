package whitehole.fileio;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

import java.io.IOException;

public class ExternalFile implements FileBase
{
    public ExternalFile(String path) throws FileNotFoundException
    {
        m_File = new RandomAccessFile(path, "rw");
        m_BigEndian = false;
    }
    
    
    @Override
    public void Save() throws IOException
    {
        m_File.getChannel().force(true);
    }

    @Override
    public void Close() throws IOException
    {
        m_File.close();
    }
    
    
    @Override
    public void ReleaseStorage()
    {
    }
    

    @Override
    public void SetBigEndian(Boolean bigendian)
    {
        m_BigEndian = bigendian;
    }
    

    @Override
    public long GetLength() throws IOException
    {
        return m_File.length();
    }

    @Override
    public void SetLength(long length) throws IOException
    {
        m_File.setLength(length);
    }
    

    @Override
    public long Position() throws IOException
    {
        return m_File.getFilePointer();
    }

    @Override
    public void Position(long newpos) throws IOException
    {
        m_File.seek(newpos);
    }
    
    @Override
    public void Skip(long nbytes) throws IOException
    {
        m_File.seek(m_File.getFilePointer() + nbytes);
    }
    

    @Override
    public byte ReadByte() throws IOException
    {
        try { return m_File.readByte(); }
        catch (EOFException ex) { return 0; }
    }

    @Override
    public short ReadShort() throws IOException
    {
        try
        {
            short ret = m_File.readShort();
            if (!m_BigEndian)
            {
                ret = (short)(((ret & 0xFF00) >>> 8) | 
                        ((ret & 0x00FF) << 8));
            }
            return ret;
        }
        catch (EOFException ex) { return 0; }
    }

    @Override
    public int ReadInt() throws IOException
    {
        try
        {
            int ret = m_File.readInt();
            if (!m_BigEndian)
            {
                ret = (int)(((ret & 0xFF000000) >>> 24) | 
                        ((ret & 0x00FF0000) >>> 8) | 
                        ((ret & 0x0000FF00) << 8) | 
                        ((ret & 0x000000FF) << 24));
            }
            return ret;
        }
        catch (EOFException ex) { return 0; }
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
        ByteBuffer bin = ByteBuffer.allocate(8);
        CharBuffer bout = CharBuffer.allocate(1);
        String ret = "";
        
        for (int i = 0; length == 0 || i < length; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                try { bin.put(m_File.readByte()); }
                catch (EOFException ex) { bin.put((byte)0); }
            }
            bin.rewind();
            
            CoderResult res = dec.decode(bin, bout, false);
            if (res == CoderResult.UNDERFLOW)
                break;
            else if (res != CoderResult.OVERFLOW)
                throw new IOException("Error while reading string: " + res);
            
            Skip(-bin.remaining());
            
            char ch = bout.get(0);
            if (ch == '\0') break;
            
            bin.clear();
            bout.clear();
            ret += ch;
        }
        
        return ret;
    }
    
    @Override
    public byte[] ReadBytes(int length) throws IOException
    {
        byte[] ret = new byte[length];
        m_File.read(ret);
        return ret;
    }
    

    @Override
    public void WriteByte(byte val) throws IOException
    {
        m_File.writeByte(val);
    }

    @Override
    public void WriteShort(short val) throws IOException
    {
        if (!m_BigEndian)
        {
            val = (short)(((val & 0xFF00) >>> 8) | 
                    ((val & 0x00FF) << 8));
        }
        m_File.writeShort(val);
    }

    @Override
    public void WriteInt(int val) throws IOException
    {
        if (!m_BigEndian)
        {
            val = (int)(((val & 0xFF000000) >>> 24) | 
                    ((val & 0x00FF0000) >>> 8) | 
                    ((val & 0x0000FF00) << 8) | 
                    ((val & 0x000000FF) << 24));
        }
        m_File.writeInt(val);
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
            for (int j = 0; j < bytesize; j++)
                m_File.writeByte(bout.get(j));
            
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
            for (int j = 0; j < bytesize; j++)
                m_File.writeByte(bout.get(j));
        }
        
        return len;
    }
    
    @Override
    public void WriteBytes(byte[] stuff) throws IOException
    {
        m_File.write(stuff);
    }
    
    
    @Override
    public byte[] GetContents() throws IOException
    {
        byte[] ret = new byte[(int)m_File.length()];
        long oldpos = m_File.getFilePointer();
        m_File.seek(0);
        m_File.read(ret);
        m_File.seek(oldpos);
        return ret;
    }
    
    @Override
    public void SetContents(byte[] buf) throws IOException
    {
        long oldpos = m_File.getFilePointer();
        m_File.setLength((long)buf.length);
        m_File.seek(0);
        m_File.write(buf);
        m_File.seek(oldpos);
    }
    
    
    protected RandomAccessFile m_File;
    private Boolean m_BigEndian;
}
