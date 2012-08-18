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

package whitehole.rendering;

import java.util.*;
import java.io.*;
import javax.media.opengl.GLContext;
import whitehole.*;
import whitehole.fileio.*;
import whitehole.smg.*;
import whitehole.vectors.*;

public class RendererCache 
{
    public static void initialize()
    {
        cache = new HashMap<>();
        refContext = null;
        contextCount = 0;
    }
    
    public static GLRenderer getObjectRenderer(GLRenderer.RenderInfo info, LevelObject obj)
    {
        String modelname = obj.name;
        // substitution: change model name
        
        String key = "object_" + obj.name;
        // substitution: add to key (for example if Obj_args matter in rendering)
        
        if (cache.containsKey(key))
        {
            CacheEntry entry = cache.get(key);
            entry.refCount++;
            return entry.renderer;
        }
        
        CacheEntry entry = new CacheEntry();
        entry.refCount = 1;
        entry.container = null;
        entry.renderer = null;
        
        // substitution: different renderer for this model
        // TODO do this more nicely
        if (modelname.equals("HeavenlyBeachPlanet"))
        {
            try
            {
                entry.container = null;
                entry.renderer = new ObjRenderer_HeavenlyBeachPlanet(info);
            }
            catch (IOException ex)
            {
                try { if (entry.container != null) entry.container.close(); } catch (IOException ex2) {}
                entry.container = null;
                entry.renderer = null;
            }
        }
        
        if (entry.renderer == null)
        {
            try
            {
                entry.container = new RarcFilesystem(Whitehole.game.filesystem.openFile("/ObjectData/"+modelname+".arc"));
                entry.renderer = new BmdRenderer(info, new Bmd(entry.container.openFile("/"+modelname+"/"+modelname+".bdl")));
            }
            catch (IOException ex)
            {
                try
                {
                    if (entry.container != null) entry.container.close();
                    entry.container = new RarcFilesystem(Whitehole.game.filesystem.openFile("/ObjectData/"+modelname+".arc"));
                    entry.renderer = new BmdRenderer(info, new Bmd(entry.container.openFile("/"+modelname+"/"+modelname+".bmd")));
                }
                catch (IOException ex2)
                {
                    try { if (entry.container != null) entry.container.close(); } catch (IOException ex3) {}
                    entry.container = null;
                    entry.renderer = null;
                }
            }
        }
        
        // failsafe default
        if (entry.renderer == null)
        {
            entry.container = null;
            entry.renderer = new ColorCubeRenderer(200f, new Color4(1f, 1f, 1f, 1f), new Color4(0f, 0f, 1f, 1f), true);
        }
        
        cache.put(key, entry);
        return entry.renderer;
    }
    
    public static void closeObjectRenderer(GLRenderer.RenderInfo info, LevelObject obj)
    {
        String key = "object_" + obj.name;
        if (!cache.containsKey(key)) return;
        
        CacheEntry entry = cache.get(key);
        entry.refCount--;
        if (entry.refCount > 0) return;
        
        entry.renderer.close(info);
        try { if (entry.container != null) entry.container.close(); } catch (IOException ex) {}
        
        cache.remove(key);
    }
    
    public static void setRefContext(GLContext ctx)
    {
        if (refContext == null) refContext = ctx;
        contextCount++;
    }
    
    public static void clearRefContext()
    {
        contextCount--;
        if (contextCount < 1) refContext = null;
    }
    
    
    public static class CacheEntry
    {
        public FilesystemBase container;
        public GLRenderer renderer;
        public int refCount;
    }
    
    public static HashMap<String, CacheEntry> cache;
    public static GLContext refContext;
    public static int contextCount;
}
