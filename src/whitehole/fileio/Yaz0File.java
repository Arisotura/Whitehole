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

import java.io.*;

public class Yaz0File extends MemoryFile
{
    public Yaz0File(FileBase backend) throws IOException
    {
        super(backend.GetContents());
        
        m_Buffer = Yaz0.Decompress(m_Buffer);
        m_Backend = backend;
        m_Backend.ReleaseStorage();
    }
    
    @Override
    public void Save() throws IOException
    {
        // TODO: recompress here?
        
        if (m_Backend != null)
        {
            m_Backend.SetContents(m_Buffer);
            m_Backend.Save();
            m_Backend.ReleaseStorage();
        }
    }
    
    @Override
    public void Close() throws IOException
    {
        if (m_Backend != null)
            m_Backend.Close();
    }
    
    
    protected FileBase m_Backend;
}
