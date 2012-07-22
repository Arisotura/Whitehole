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

public class RarcFile extends MemoryFile
{
    public RarcFile(RarcFilesystem fs, int id) throws IOException
    {
        super(fs.GetFileContents(id));
        
        m_FS = fs;
        m_ID = id;
    }

    @Override
    public void Save() throws IOException
    {
        m_FS.ReinsertFile(this);
    }


    private RarcFilesystem m_FS;
    private int m_ID;

    public int ID() { return m_ID; }
}
