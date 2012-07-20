package whitehole.fileio;

import java.io.*;
import java.util.*;

public class ExternalFilesystem implements FilesystemBase
{
    public ExternalFilesystem(String basedir) throws IOException
    {
        if (basedir.endsWith("/") || basedir.endsWith("\\"))
            basedir = basedir.substring(0, basedir.length() - 1);

        m_BaseDirectory = new File(basedir);
        if (!m_BaseDirectory.exists()) throw new IOException("Directory '" + basedir + "' doesn't exist");
        if (!m_BaseDirectory.isDirectory()) throw new IOException(basedir + " isn't a directory");
    }
    
    @Override
    public void Close()
    {
    }


    @Override
    public String[] GetDirectories(String directory)
    {
        directory = directory.substring(1);
        
        File[] files = new File(m_BaseDirectory, directory).listFiles();
        List<String> ret = new ArrayList<>();

        for (File file: files)
        {
            if (!file.isDirectory()) continue;
            ret.add(file.getName());
        }

        return Arrays.copyOf(ret.toArray(), ret.size(), String[].class);
    }

    @Override
    public Boolean DirectoryExists(String directory)
    {
        directory = directory.substring(1);
        
        File dir = new File(m_BaseDirectory, directory);
        return dir.exists() && dir.isDirectory();
    }


    @Override
    public String[] GetFiles(String directory)
    {
        directory = directory.substring(1);
        
        File[] files = new File(m_BaseDirectory, directory).listFiles();
        List<String> ret = new ArrayList<>();

        for (File file: files)
        {
            if (!file.isFile()) continue;
            ret.add(file.getName());
        }

        return Arrays.copyOf(ret.toArray(), ret.size(), String[].class);
    }

    @Override
    public Boolean FileExists(String filename)
    { 
        filename = filename.substring(1);
        
        File file = new File(m_BaseDirectory, filename);
        return file.exists() && file.isFile();
    }

    @Override
    public FileBase OpenFile(String filename) throws FileNotFoundException
    {
        return new ExternalFile(m_BaseDirectory.getPath() + filename);
    }


    private File m_BaseDirectory;
}
