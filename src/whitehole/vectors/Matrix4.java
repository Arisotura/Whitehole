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

public class Matrix4 
{
    public Matrix4()
    {
        M = new float[16];
        M[0] = 1f; M[1] = 0f; M[2] = 0f; M[3] = 0f;
        M[4] = 0f; M[5] = 1f; M[6] = 0f; M[7] = 0f;
        M[8] = 0f; M[9] = 0f; M[10] = 1f; M[11] = 0f;
        M[12] = 0f; M[13] = 0f; M[14] = 0f; M[15] = 1f;
    }
    
    public Matrix4(float m0, float m1, float m2, float m3, 
            float m4, float m5, float m6, float m7,
            float m8, float m9, float m10, float m11,
            float m12, float m13, float m14, float m15)
    {
        M = new float[16];
        M[0] = m0; M[1] = m1; M[2] = m2; M[3] = m3;
        M[4] = m4; M[5] = m5; M[6] = m6; M[7] = m7;
        M[8] = m8; M[9] = m9; M[10] = m10; M[11] = m11;
        M[12] = m12; M[13] = m13; M[14] = m14; M[15] = m15;
    }
    
    
    public static Matrix4 scale(Vector3 factor)
    {
        return new Matrix4(
                factor.X, 0f, 0f, 0f,
                0f, factor.Y, 0f, 0f,
                0f, 0f, factor.Z, 0f,
                0f, 0f, 0f, 1f);
    }
    
    
    public static Matrix4 createRotationX(float angle)
    {
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        
        return new Matrix4(
                1f, 0f, 0f, 0f,
                0f, cos, sin, 0f,
                0f, -sin, cos, 0f,
                0f, 0f, 0f, 1f);
    }
    
    public static Matrix4 createRotationY(float angle)
    {
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        
        return new Matrix4(
                cos, 0f, -sin, 0f,
                0f, 1f, 0f, 0f,
                sin, 0f, cos, 0f,
                0f, 0f, 0f, 1f);
    }
    
    public static Matrix4 createRotationZ(float angle)
    {
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        
        return new Matrix4(
                cos, sin, 0f, 0f,
                -sin, cos, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f);
    }
    
    
    public static Matrix4 createTranslation(Vector3 trans)
    {
        return new Matrix4(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                trans.X, trans.Y, trans.Z, 1f);
    }
    
    
    public static Matrix4 mult(Matrix4 left, Matrix4 right)
    {
        return new Matrix4(
                left.M[0] * right.M[0] + left.M[1] * right.M[4] + left.M[2] * right.M[8] + left.M[3] * right.M[12],
                left.M[0] * right.M[1] + left.M[1] * right.M[5] + left.M[2] * right.M[9] + left.M[3] * right.M[13],
                left.M[0] * right.M[2] + left.M[1] * right.M[6] + left.M[2] * right.M[10] + left.M[3] * right.M[14],
                left.M[0] * right.M[3] + left.M[1] * right.M[7] + left.M[2] * right.M[11] + left.M[3] * right.M[15],
                
                left.M[4] * right.M[0] + left.M[5] * right.M[4] + left.M[6] * right.M[8] + left.M[7] * right.M[12],
                left.M[4] * right.M[1] + left.M[5] * right.M[5] + left.M[6] * right.M[9] + left.M[7] * right.M[13],
                left.M[4] * right.M[2] + left.M[5] * right.M[6] + left.M[6] * right.M[10] + left.M[7] * right.M[14],
                left.M[4] * right.M[3] + left.M[5] * right.M[7] + left.M[6] * right.M[11] + left.M[7] * right.M[15],
                
                left.M[8] * right.M[0] + left.M[9] * right.M[4] + left.M[10] * right.M[8] + left.M[11] * right.M[12],
                left.M[8] * right.M[1] + left.M[9] * right.M[5] + left.M[10] * right.M[9] + left.M[11] * right.M[13],
                left.M[8] * right.M[2] + left.M[9] * right.M[6] + left.M[10] * right.M[10] + left.M[11] * right.M[14],
                left.M[8] * right.M[3] + left.M[9] * right.M[7] + left.M[10] * right.M[11] + left.M[11] * right.M[15],
                
                left.M[12] * right.M[0] + left.M[13] * right.M[4] + left.M[14] * right.M[8] + left.M[15] * right.M[12],
                left.M[12] * right.M[1] + left.M[13] * right.M[5] + left.M[14] * right.M[9] + left.M[15] * right.M[13],
                left.M[12] * right.M[2] + left.M[13] * right.M[6] + left.M[14] * right.M[10] + left.M[15] * right.M[14],
                left.M[12] * right.M[3] + left.M[13] * right.M[7] + left.M[14] * right.M[11] + left.M[15] * right.M[15]);
    }
    
    
    public float M[];
}
