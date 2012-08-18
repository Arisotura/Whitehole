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
import java.util.*;
import whitehole.fileio.*;

public class ZoneArchive 
{
    public ZoneArchive(GalaxyArchive arc, String name) throws IOException
    {
        galaxy = arc;
        game = arc.game;
        filesystem = game.filesystem;
        
        zoneName = name;
        
        String zonefile = "lolz";
        
        // try SMG2-style first, then SMG1
        if (filesystem.fileExists("/StageData/" + zoneName + "/" + zoneName + "Map.arc"))
        {
            // SMG2-style zone
            // * /StageData/<zoneName>/<zoneName>Design.arc -> ???
            // * /StageData/<zoneName>/<zoneName>Map.arc -> holds map objects
            // * /StageData/<zoneName>/<zoneName>Sound.arc -> seems to hold sound-related objects
            
            gameMask = 2;
            zonefile = "/StageData/" + zoneName + "/" + zoneName + "Map.arc";
        }
        else
        {
            // SMG1-style zone
            // * /StageData/<zoneName>.arc -> holds all map objects
            
            gameMask = 1;
            zonefile = "/StageData/" + zoneName + ".arc";
        }
        
        objects = new HashMap<>();
        subZones = new HashMap<>();
        RarcFilesystem zonearc = new RarcFilesystem(filesystem.openFile(zonefile));
        
        loadObjects(zonearc, "MapParts", "MapPartsInfo");
        loadObjects(zonearc, "Placement", "ObjInfo");
        
        loadSubZones(zonearc);
        
        zonearc.close();
    }
    
    public void close()
    {
    }
    
    
    private void loadObjects(RarcFilesystem arc, String dir, String file)
    {
        List<String> layers = arc.getDirectories("/Stage/Jmp/" + dir);
        for (String layer : layers)
            addObjectsToList(arc, dir + "/" + layer + "/" + file);
    }
    
    private void addObjectsToList(RarcFilesystem arc, String filepath)
    {
        String layer = filepath.split("/")[1].toLowerCase();
        
        if (!objects.containsKey(layer))
            objects.put(layer, new ArrayList<LevelObject>());
        
        try
        {
            Bcsv bcsv = new Bcsv(arc.openFile("/Stage/Jmp/" + filepath));
            for (Bcsv.Entry entry : bcsv.entries)
                objects.get(layer).add(new LevelObject(zoneName, filepath, entry));
            bcsv.close();
        }
        catch (IOException ex)
        {
            // TODO better error handling, really
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void loadSubZones(RarcFilesystem arc)
    {
        List<String> layers = arc.getDirectories("/Stage/Jmp/Placement");
        for (String layer : layers)
        {
            try
            {
                Bcsv bcsv = new Bcsv(arc.openFile("/Stage/Jmp/Placement/" + layer + "/StageObjInfo"));
                subZones.put(layer.toLowerCase(), bcsv.entries); // lazy lol
                bcsv.close();
            }
            catch (IOException ex)
            {
                // TODO better error handling, really
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    
    public GalaxyArchive galaxy;
    public GameArchive game;
    public FilesystemBase filesystem;
    
    public String zoneName;
    
    public int gameMask;
    public HashMap<String, List<LevelObject>> objects;
    public HashMap<String, List<Bcsv.Entry>> subZones;
}
