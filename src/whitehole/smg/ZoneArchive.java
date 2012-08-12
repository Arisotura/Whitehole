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

package whitehole.smg;

import java.io.*;
import whitehole.fileio.*;

public class ZoneArchive 
{
    public ZoneArchive(GalaxyArchive arc, String name) throws IOException
    {
        galaxy = arc;
        game = arc.game;
        filesystem = game.filesystem;
        
        zoneName = name;
        
        // try SMG2-style first, then SMG1
        if (filesystem.directoryExists("/StageData/" + zoneName))
        {
            // SMG2-style zone
            // * /StageData/<zoneName>/<zoneName>Design.arc -> ???
            // * /StageData/<zoneName>/<zoneName>Map.arc -> holds map objects
            // * /StageData/<zoneName>/<zoneName>Sound.arc -> seems to hold sound-related objects
        }
        else
        {
            // SMG1-style zone
            // * /StageData/<zoneName>.arc -> holds all map objects
        }
    }
    
    public void close()
    {
    }
    
    
    public GalaxyArchive galaxy;
    public GameArchive game;
    public FilesystemBase filesystem;
    
    public String zoneName;
}
