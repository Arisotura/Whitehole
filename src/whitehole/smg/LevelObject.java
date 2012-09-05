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

import javax.media.opengl.GL2;
import whitehole.*;
import whitehole.rendering.*;
import whitehole.vectors.Vector3;

public class LevelObject
{
    public LevelObject(String zone, String filepath, Bcsv.Entry entry)
    {
        this.zone = zone;
        String[] stuff = filepath.split("/");
        directory = stuff[0];
        layer = stuff[1].toLowerCase();
        file = stuff[2];
        
        data = entry;
        
        name = (String)data.get("name");
        loadDBInfo();
        renderer = null;
        
        uniqueID = -1;
        
        position = new Vector3((float)data.get("pos_x"), (float)data.get("pos_y"), (float)data.get("pos_z"));
        rotation = new Vector3((float)data.get("dir_x"), (float)data.get("dir_y"), (float)data.get("dir_z"));
        scale = new Vector3((float)data.get("scale_x"), (float)data.get("scale_y"), (float)data.get("scale_z"));
    }
    
    public LevelObject(String zone, String filepath, int game, String objname, Vector3 pos)
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
        
        position = pos;
        rotation = new Vector3(0f, 0f, 0f);
        scale = new Vector3(1f, 1f, 1f);
        
        data.put("name", name);
        data.put("pos_x", position.x); data.put("pos_y", position.y); data.put("pos_z", position.z);
        data.put("dir_x", rotation.x); data.put("dir_y", rotation.y); data.put("dir_z", rotation.z);
        data.put("scale_x", scale.x); data.put("scale_y", scale.y); data.put("scale_z", scale.z);
        
        data.put("Obj_arg0", -1);
        data.put("Obj_arg1", -1);
        data.put("Obj_arg2", -1);
        data.put("Obj_arg3", -1);
        if (file.equalsIgnoreCase("objinfo"))
        {
            data.put("Obj_arg4", -1);
            data.put("Obj_arg5", -1);
            data.put("Obj_arg6", -1);
            data.put("Obj_arg7", -1);
        }
        
        data.put("l_id", 0);
        data.put("CameraSetId", 0);
        data.put("SW_APPEAR", -1);
        data.put("SW_DEAD", -1);
        data.put("SW_A",  -1);
        data.put("SW_B", -1);
        if (game == 2)
        {
            data.put("SW_AWAKE", -1);
            data.put("SW_PARAM", -1);
            data.put("ParamScale", 1f);
        }
        else
            data.put(0x4F11491C, -1);
        data.put("CastId", 0);
        data.put("ViewGroupId", 0);
        data.put("ShapeModelNo", (short)0);
        data.put("CommonPath_ID", (short)-1);
        data.put("ClippingGroupId", (short)0);
        data.put("GroupId", (short)0);
        data.put("DemoGroupId", (short)0);
        if (game == 2 || file.equalsIgnoreCase("objinfo"))
            data.put("MapParts_ID", (short)-1);
        if (game == 2)
            data.put("Obj_ID", (short)-1);
        
        if (file.equalsIgnoreCase("objinfo"))
        {
            data.put("MessageId", -1);
            if (game == 2)
                data.put("GeneratorID", (short)-1);
        }
        
        if (file.equalsIgnoreCase("mappartsinfo"))
        {
            data.put("MoveConditionType", 0);
            data.put("RotateSpeed", 0);
            data.put("RotateAngle", 0);
            data.put("RotateAxis", 0);
            data.put("RotateAccelType", 0);
            data.put("RotateStopTime", 0);
            data.put("RotateType", 0);
            data.put("ShadowType", 0);
            data.put("SignMotionType", 0);
            data.put(0x4137EDFD, -1);
            data.put("FarClip", -1);
            if (game == 2)
                data.put("ParentId", (short)-1);
        }
    }
    
    public void save()
    {
        data.put("name", name);
        data.put("pos_x", position.x); data.put("pos_y", position.y); data.put("pos_z", position.z);
        data.put("dir_x", rotation.x); data.put("dir_y", rotation.y); data.put("dir_z", rotation.z);
        data.put("scale_x", scale.x); data.put("scale_y", scale.y); data.put("scale_z", scale.z);
    }
    
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    public final void loadDBInfo()
    {
        if (ObjectDB.objects.containsKey(name))
            dbInfo = ObjectDB.objects.get(name);
        else
        {
            dbInfo = new ObjectDB.Object();
            dbInfo.ID = name;
            dbInfo.name = "("+name+")";
            dbInfo.category = 0;
            dbInfo.games = 3;
        }
    }
    
    public void initRenderer(GLRenderer.RenderInfo info)
    {
        if (renderer != null) return;
        renderer = RendererCache.getObjectRenderer(info, this);
        renderer.compileDisplayLists(info);
        renderer.releaseStorage();
    }
    
    public void closeRenderer(GLRenderer.RenderInfo info)
    {
        if (renderer == null) return;
        RendererCache.closeObjectRenderer(info, this);
        renderer = null;
    }
    
    public void render(GLRenderer.RenderInfo info)
    {
        GL2 gl = info.drawable.getGL().getGL2();
        
        gl.glPushMatrix();
        
        gl.glTranslatef(position.x, position.y, position.z);
        gl.glRotatef(rotation.z, 0f, 0f, 1f);
        gl.glRotatef(rotation.y, 0f, 1f, 0f);
        gl.glRotatef(rotation.x, 1f, 0f, 0f);
        if (renderer.isScaled())
            gl.glScalef(scale.x, scale.y, scale.z);
        
        int dlid = -1;
        switch (info.renderMode)
        {
            case PICKING: dlid = 0; break;
            case OPAQUE: dlid = 1; break;
            case TRANSLUCENT: dlid = 2; break;
        }
        
        gl.glCallList(renderer.displayLists[dlid]);
        
        gl.glPopMatrix();
    }
    
    
    public String zone, directory, layer, file;
    public String name;
    public Bcsv.Entry data;
    public ObjectDB.Object dbInfo;
    public GLRenderer renderer;
    
    public int uniqueID;
    
    public Vector3 position, rotation, scale;
}
