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
