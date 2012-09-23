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

package whitehole.rendering;

import java.io.IOException;
import whitehole.smg.LevelObject;
import whitehole.vectors.Vector3;

public class ObjectModelSubstitutor 
{
    public static String substituteModelName(LevelObject obj, String modelname)
    {
        switch (obj.name)
        {
            case "BenefitItemOneUp": return "KinokoOneUp";
            case "SplashPieceBlock": return "CoinBlock";
        }
        
        return modelname;
    }
    
    public static String substituteObjectKey(LevelObject obj, String objectkey)
    {
        if (obj.name.equals("Pole")) objectkey += String.format("_%1$3f", obj.scale.y / obj.scale.x);
        return objectkey;
    }
    
    public static GLRenderer substituteRenderer(LevelObject obj, GLRenderer.RenderInfo info)
    {
        try
        {
            switch (obj.name)
            {
                case "Patakuri": return new DoubleBmdRenderer(info, "Kuribo", new Vector3(), "PatakuriWing", new Vector3(0f,15f,-25f));
                case "Pole": return new ObjRenderer_Pole(info, obj.scale);
                    
                case "FlagKoopaA": return new BtiRenderer(info, "FlagKoopaA", new Vector3(0f,150f,0f), new Vector3(0f,-150f,600f), true);
                    
                case "HeavenlyBeachPlanet": return new DoubleBmdRenderer(info, "HeavenlyBeachPlanet", new Vector3(), "HeavenlyBeachPlanetWater", new Vector3());
                //case "OceanBowl": return new ObjRenderer_OceanBowl(info); // too glitchy.
            }
        }
        catch (IOException ex) {}
        
        return null;
    }
}
