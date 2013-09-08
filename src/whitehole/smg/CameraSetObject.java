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
    * 
    * UNUSED. Crashes upon startup. I need to see why it does this. 
    * Probably since it can't read the bcam file. Blah, moar support needed.


package whitehole.smg;

import whitehole.PropertyGrid;
import whitehole.vectors.Vector3;

public class CameraSetObject extends LevelObject
{
    public CameraSetObject(ZoneArchive zone, String filepath, Bcsv.Entry entry)
    {
        this.zone = zone;
        String[] stuff = filepath.split("/");
        directory = stuff[0];
        layer = stuff[1].toLowerCase();
        file = stuff[2];
        
        data = entry;
        
        name = (String)data.get("camtype");
        loadDBInfo();
        renderer = null;
        
        uniqueID = -1;
        
        // Nothing past here. No coordinates.
    }
    
    public CameraSetObject(ZoneArchive zone, String filepath, int game, String objname, Vector3 pos)
    {
        this.zone = zone;
        String[] stuff = filepath.split("/");
        directory = stuff[0];
        layer = stuff[1].toLowerCase();
        file = stuff[2];
        
        data = new Bcsv.Entry();
        
        name = objname;
        loadDBInfo();
        renderer = null;
        
        uniqueID = -1;
        
        
        data.put("version", -1);
        data.put("g.flag.thru", -1);
        data.put("num2", -1);
        data.put("num1", -1);  
        data.put("angleB", -1);
        data.put("angleA", -1);
        data.put("dist", -1);
        data.put("vpanaxis.X", -1);  
        data.put("vpanaxis.Y", -1);
        data.put("vpanaxis.Z", -1);
        data.put("vpanuse", -1);
        data.put("udown", -1);  
        data.put("pushdelaylow", -1);
        data.put("pushdelay", -1);
        data.put("lplay", -1);
        data.put("uplay", -1);
        data.put("gndint", -1);
        data.put("lower", -1);
        data.put("upper", -1);
        data.put("camint", -1);
        data.put("fovy", -1);
        data.put("roll", -1);
        data.put("loffset", -1);
        data.put("xoffset.X", -1);
        data.put("woffset.Y", -1);
        data.put("woffset.Z", -1);
        data.put("camtype", -1);
        data.put("id", -1);
        data.put("evpriority", -1);
        data.put("evfrm", -1);
        data.put("camendint", -1);
        data.put("eflag.enableEndErpFrame", -1);
        data.put("eflag.enableErpFrame", -1);
        data.put("axis.X", -1);
        data.put("axis.Y", -1);
        data.put("axis.Z", -1);

    }
    
    @Override
    public void save()
    {
           // Uhh..not used. This section doesn't use coordinates.
    }

    
    @Override
    public void getProperties(PropertyGrid panel)
    {
        panel.addCategory("obj_position", "Camera Settings");
        panel.addField("version", "Version", "float", null, data.get("version"));
        panel.addField("g.flag.thru", "g.flag.trhu", "float", null, data.get ("g.flag.thru"));
        panel.addField("num2", "Num 2", "float", null, data.get ("num2"));
        panel.addField("num1", "Num 1", "float", null, data.get ("num1"));
        panel.addField("angleB", "Angle B", "float", null, data.get ("angleB"));
        panel.addField("angleA", "Angle A", "float", null, data.get ("angleA"));
        panel.addField("dist", "Distance", "float", null, data.get ("dist"));
        panel.addField("vpanaxisX", "Pan Axis X", "float", null, data.get ("vpanaxisX"));
        panel.addField("vpanaxisY", "Pan Axis Y", "float", null, data.get ("vpanaxisY"));
        panel.addField("vpanaxisZ", "Pan Axis Z", "float", null, data.get ("vpanaxisZ"));
        panel.addField("vpanuse", "Pan Use", "float", null, data.get ("vpanuse"));
        panel.addField("udown", "U Down", "float", null, data.get ("udown"));
        panel.addField("pushdelaylow", "Push Delay Low", "float", null, data.get ("pushdelaylow"));
        panel.addField("pushdelay", "Push Delay", "float", null, data.get ("pushdelay"));
        panel.addField("lplay", "L Play", "float", null, data.get ("lplay"));
        panel.addField("uplay", "U Play", "float", null, data.get ("uplay"));
        panel.addField("gndint", "gndint", "float", null, data.get ("gndint"));
        panel.addField("lower", "Lower", "float", null, data.get ("lower"));
        panel.addField("upper", "Upper", "float", null, data.get ("upper"));
        panel.addField("camint", "Camint", "float", null, data.get ("camint"));
        panel.addField("fovy", "Fovy", "float", null, data.get ("fovy"));
        panel.addField("roll", "Roll", "float", null, data.get ("roll"));
        panel.addField("loffset", "L Offset", "float", null, data.get ("loffset"));
        panel.addField("woffset.X", "Offset X", "float", null,data.get ("woffset.x"));
        panel.addField("woffset.Y", "Offset Y", "float", null,data.get ("woffset.y"));
        panel.addField("woffset.Z", "Offset Z", "float", null, data.get ("woffset.z"));
        panel.addField("camtype", "Camera Type", "float", null, data.get ("camtype"));
        panel.addField("id", "ID", "float", null, data.get  ("id"));
        panel.addField("eflag.enableEndErpFrame", "End Frame", "float", null, data.get ("eflag.enableEndErpFrame"));
        panel.addField("eflag.enableErpFrame", "Enable Frame", "float", null, data.get ("eflag.enableErpFrame"));
        panel.addField("axis.X", "Axis X", "float", null, data.get ("axis.X"));
        panel.addField("axis.Y", "Axis Y", "float", null, data.get ("axis.Y"));
        panel.addField("axis.Z", "Axis Z", "float", null, data.get ("axis.Z"));

    }
}
*/
