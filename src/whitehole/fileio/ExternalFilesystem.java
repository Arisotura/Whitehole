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
    public List<String> GetDirectories(String directory)
    {
        directory = directory.substring(1);
        
        File[] files = new File(m_BaseDirectory, directory).listFiles();
        List<String> ret = new ArrayList<>();

        for (File file: files)
        {
            if (!file.isDirectory()) continue;
            ret.add(file.getName());
        }

        return ret;
    }

    @Override
    public Boolean DirectoryExists(String directory)
    {
        directory = directory.substring(1);
        
        File dir = new File(m_BaseDirectory, directory);
        return dir.exists() && dir.isDirectory();
    }


    @Override
    public List<String> GetFiles(String directory)
    {
        directory = directory.substring(1);
        
        File[] files = new File(m_BaseDirectory, directory).listFiles();
        List<String> ret = new ArrayList<>();

        for (File file: files)
        {
            if (!file.isFile()) continue;
            ret.add(file.getName());
        }

        return ret;
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
