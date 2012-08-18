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

import javax.media.opengl.GL2;
import whitehole.*;
import whitehole.rendering.*;

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
        dbInfo = ObjectDB.objects.get(name);
        renderer = null;
        
        uniqueID = -1;
    }
    
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    public void initRenderer(GLRenderer.RenderInfo info)
    {
        if (renderer != null) return;
        renderer = RendererCache.getObjectRenderer(info, this);
        renderer.compileDisplayLists(info);
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
        
        gl.glTranslatef((float)data.get("pos_x"), (float)data.get("pos_y"), (float)data.get("pos_z"));
        gl.glRotatef((float)data.get("dir_z"), 0f, 0f, 1f);
        gl.glRotatef((float)data.get("dir_y"), 0f, 1f, 0f);
        gl.glRotatef((float)data.get("dir_x"), 1f, 0f, 0f);
        gl.glScalef((float)data.get("scale_x"), (float)data.get("scale_y"), (float)data.get("scale_z"));
        
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
}
