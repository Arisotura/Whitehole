/*
    Copyright 2012 The Whitehole team

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

package whitehole.smg;

import java.util.*;
import java.io.*;
import whitehole.fileio.*;

public class GameArchive 
{
    public GameArchive(FilesystemBase fs)
    {
        filesystem = fs;
    }
    
    public void close()
    {
        try { filesystem.close(); } catch (IOException ex) {}
    }
    
    
    public boolean galaxyExists(String name)
    {
        return filesystem.fileExists(String.format("/StageData/%1$s/%1$sScenario.arc", name));
    }
    
    public List<String> getGalaxies()
    {
        List<String> ret = new ArrayList<>();
        
        List<String> stages = filesystem.getDirectories("/StageData");
        for (String stage : stages)
        {
            if (!galaxyExists(stage))
                continue;
            
            ret.add(stage);
        }
        
        return ret;
    }
    
    public GalaxyArchive openGalaxy(String name) throws IOException
    {
        if (!galaxyExists(name)) return null;
        return new GalaxyArchive(this, name);
    }
    
    /*public FileBase openGalaxyFile(String galaxy, String file)
    {
        try
        {
            return filesystem.openFile(String.format("/StageData/%1$s/%1$s%2$s.arc", galaxy, file));
        }
        catch (FileNotFoundException ex)
        {
            return null;
        }
    }*/
    
    
    public FilesystemBase filesystem;
}
