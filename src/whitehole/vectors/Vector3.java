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
        X = Y = Z = 0f;
    }
    
    public Vector3(float x, float y, float z)
    {
        X = x;
        Y = y;
        Z = z;
    }
    
    
    public static Vector3 transform(Vector3 v, Matrix4 m)
    {
        return new Vector3(
                v.X * m.M[0] + v.Y * m.M[4] + v.Z * m.M[8] + m.M[12],
                v.X * m.M[1] + v.Y * m.M[5] + v.Z * m.M[9] + m.M[13],
                v.X * m.M[2] + v.Y * m.M[6] + v.Z * m.M[10] + m.M[14]);
    }
    
    
    public float X, Y, Z;
}
