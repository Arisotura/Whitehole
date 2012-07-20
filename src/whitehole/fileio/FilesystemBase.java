package whitehole.fileio;

import java.io.*;

public interface FilesystemBase
{
    public void Close() throws IOException;
    
    public String[] GetDirectories(String directory);
    public Boolean DirectoryExists(String directory);
    
    public String[] GetFiles(String directory);
    public Boolean FileExists(String directory);
    public FileBase OpenFile(String filename) throws FileNotFoundException;
}
