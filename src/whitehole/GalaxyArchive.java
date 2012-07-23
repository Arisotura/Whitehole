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

package whitehole;

import java.util.*;
import java.io.*;
import whitehole.fileio.*;

public class GalaxyArchive 
{
    public GalaxyArchive(GameArchive arc, String name)
    {
        game = arc;
        galaxyName = name;
        
        try
        {
            zoneList = new ArrayList<>();
            RarcFilesystem scenario = new RarcFilesystem(game.openGalaxyFile(galaxyName, "Scenario"));
            
            Bcsv zonelist = new Bcsv(scenario.openFile(String.format("/%1$sScenario/ZoneList.bcsv", galaxyName)));
            for (Bcsv.Entry entry : zonelist.entries)
            {
                zoneList.add((String)entry.get("ZoneName"));
            }
            
            // todo moar crap
        }
        catch (IOException ex)
        {
        }
    }
    
    
    public List<String> getZones()
    {
        return zoneList;
    }
    
    
    private GameArchive game;
    public String galaxyName;
    private List<String> zoneList;
}
