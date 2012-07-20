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
