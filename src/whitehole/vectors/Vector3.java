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

package whitehole.vectors;

public class Vector3 
{
    public Vector3()
    {
        x = y = z = 0f;
    }
    
    public Vector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    
    public static Vector3 transform(Vector3 v, Matrix4 m)
    {
        return new Vector3(
                v.x * m.m[0] + v.y * m.m[4] + v.z * m.m[8] + m.m[12],
                v.x * m.m[1] + v.y * m.m[5] + v.z * m.m[9] + m.m[13],
                v.x * m.m[2] + v.y * m.m[6] + v.z * m.m[10] + m.m[14]);
    }
    
    public float length()
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }
    
    public static Vector3 normalize(Vector3 v)
    {
        float len = v.length();
        if (len < 0.000001f) len = 1f;
        return new Vector3(
                v.x / len,
                v.y / len,
                v.z / len);
    }
    
    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    public static Vector3 subtract(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    public static Vector3 cross(Vector3 a, Vector3 b)
    {
        return new Vector3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x);
    }
    
    
    public float x, y, z;
}
