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

import whitehole.vectors.Vector3;

public class PathPointObject 
{
    public PathPointObject(PathObject path, Bcsv.Entry entry)
    {
        this.path = path;
        
        data = entry;
        uniqueID = -1;
        
        index = (int)(short)data.get("id");
        point0 = new Vector3((float)data.get("pnt0_x"), (float)data.get("pnt0_y"), (float)data.get("pnt0_z"));
        point1 = new Vector3((float)data.get("pnt1_x"), (float)data.get("pnt1_y"), (float)data.get("pnt1_z"));
        point2 = new Vector3((float)data.get("pnt2_x"), (float)data.get("pnt2_y"), (float)data.get("pnt2_z"));
    }
    
    @Override
    public String toString()
    {
        return String.format("Point %1$d", index);
    }
    
    
    public PathObject path;
    public Bcsv.Entry data;
    
    public int uniqueID;
    
    public int index;
    public Vector3 point0, point1, point2;
}
