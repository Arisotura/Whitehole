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

import java.io.IOException;
import whitehole.smg.LevelObject;

public class ObjectModelSubstitutor 
{
    public static String substituteModelName(LevelObject obj, String modelname)
    {
        return modelname;
    }
    
    public static String substituteObjectKey(LevelObject obj, String objectkey)
    {
        return objectkey;
    }
    
    public static GLRenderer substituteRenderer(LevelObject obj, GLRenderer.RenderInfo info)
    {
        try
        {
            switch (obj.name)
            {
                case "HeavenlyBeachPlanet": return new ObjRenderer_HeavenlyBeachPlanet(info);
            }
        }
        catch (IOException ex) {}
        
        return null;
    }
}
