/*
    Copyright 2012 Mega-Mario

    This file is part of Whitehole.

    Whitehole is free software: you can redistribute it and/or modify it under
    the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    Whitehole is distributed in the hope that it will be useful, but WITHOUT ANY 
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
    FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along 
    with Whitehole. If not, see http://www.gnu.org/licenses/.
*/

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
    public int WriteString(String encoding, String val, int length) throws IOException;
    public void WriteBytes(byte[] stuff) throws IOException;
    
    public byte[] GetContents() throws IOException;
    public void SetContents(byte[] buf) throws IOException;
}
