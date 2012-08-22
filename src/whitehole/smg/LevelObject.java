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
        layer = stuff[1];
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
    
    public void save()
    {
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
