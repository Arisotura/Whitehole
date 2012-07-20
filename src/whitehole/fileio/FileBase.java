package whitehole.fileio;

import java.io.IOException;

public interface FileBase 
{
    public void Save() throws IOException;      // Forces changes to the file's contents to be saved
    public void Close() throws IOException;     // Closes the file
    
    // Typically used by classes extending MemoryFile, to save RAM. Has no effect on ExternalFiles.
    public void ReleaseStorage();
    
    // Affects the byte ordering of the Read/Write methods
    public void SetBigEndian(Boolean bigendian);
    
    public long GetLength() throws IOException;
    public void SetLength(long length) throws IOException;
    
    public long Position() throws IOException;
    public void Position(long newpos) throws IOException;
    public void Skip(long nbytes) throws IOException;
    
    public byte ReadByte() throws IOException;
    public short ReadShort() throws IOException;
    public int ReadInt() throws IOException;
    public float ReadFloat() throws IOException;
    public String ReadString(String encoding, int length) throws IOException;
    public byte[] ReadBytes(int length) throws IOException;
    
    public void WriteByte(byte val) throws IOException;
    public void WriteShort(short val) throws IOException;
    public void WriteInt(int val) throws IOException;
    public void WriteFloat(float val) throws IOException;
    public void WriteString(String encoding, String val, int length) throws IOException;
    public void WriteBytes(byte[] stuff) throws IOException;
    
    public byte[] GetContents() throws IOException;
    public void SetContents(byte[] buf) throws IOException;
}
