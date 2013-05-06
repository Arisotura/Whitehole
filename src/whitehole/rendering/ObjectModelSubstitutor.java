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
import whitehole.smg.GravityObject;
import whitehole.smg.LevelObject;
import whitehole.vectors.Color4;
import whitehole.vectors.Vector3;

public class ObjectModelSubstitutor 
{
    public static String substituteModelName(LevelObject obj, String modelname)
    {
        switch (obj.name)
        {
            case "BenefitItemOneUp": return "KinokoOneUp";
            case "SplashPieceBlock": return "CoinBlock";
                
            case "Karikari": return "Karipon";
                
            case "JetTurtle": return "Koura";
        }
        //modelname="VROrbit";//"CollapsePlane";
        return modelname;
    }
    
    public static String substituteObjectKey(LevelObject obj, String objectkey)
    {
        switch (obj.name)
        {
            case "Pole": objectkey += String.format("_%1$3f", obj.scale.y / obj.scale.x); break;
                
            case "Kinopio": 
            case "KinopioAstro": objectkey = String.format("object_Kinopio_%1$d", obj.data.get("Obj_arg1")); break;
                
            case "UFOKinoko": objectkey = String.format("object_UFOKinoko_%1$d", obj.data.get("Obj_arg0")); break;
                
            case "AstroDome":
            case "AstroDomeEntrance":
            case "AstroDomeSky":
            case "AstroStarPlate": objectkey += String.format("_%1$d", obj.data.get("Obj_arg0")); break;
        }
        //objectkey = "CollapsePlane";
        return objectkey;
    }
    
    public static GLRenderer substituteRenderer(LevelObject obj, GLRenderer.RenderInfo info)
    {
        try
        {
            if (obj.getClass() == GravityObject.class)
                return new ColorCubeRenderer(100f, new Color4(1f, 0.5f, 0.5f, 1f), new Color4(0.8f, 0f, 0f, 1f), true);
            
            switch (obj.name)
            {
                case "Patakuri": return new DoubleBmdRenderer(info, "Kuribo", new Vector3(), "PatakuriWing", new Vector3(0f,15f,-25f));
                case "Kinopio": 
                case "KinopioAstro": return new ObjRenderer_Kinopio(info, (int)obj.data.get("Obj_arg1"));
                    
                case "UFOKinoko": return new ObjRenderer_UFOKinoko(info, (int)obj.data.get("Obj_arg0"));
                    
                case "Pole": return new ObjRenderer_Pole(info, obj.scale);
                    
                case "FlagKoopaA": return new BtiRenderer(info, "FlagKoopaA", new Vector3(0f,150f,0f), new Vector3(0f,-150f,600f), true);
                    
                case "AstroDome":
                case "AstroDomeEntrance":
                case "AstroDomeSky":
                case "AstroStarPlate":
                    return new ObjRenderer_AstroPart(info, obj.name, (int)obj.data.get("Obj_arg0"));
                    
                case "RedBlueTurnBlock": return new DoubleBmdRenderer(info, "RedBlueTurnBlock", new Vector3(), "RedBlueTurnBlockBase", new Vector3());
            }
        }
        catch (IOException ex) {}
        
        return null;
    }
}
