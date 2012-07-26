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

package whitehole;

import javax.media.opengl.*;
import whitehole.vectors.*;

public class ColorCubeRenderer implements Renderer
{
    public ColorCubeRenderer(float size, Color4 border, Color4 fill, Boolean axes)
    {
        cubeSize = size;
        borderColor = border;
        fillColor = fill;
        showAxes = axes;
    }
    
    @Override
    public void close(RenderInfo info) throws GLException
    {
    }

    @Override
    public Boolean gottaRender(RenderInfo info) throws GLException
    {
        return info.renderMode != RenderMode.TRANSLUCENT;
    }

    @Override
    public void render(RenderInfo info) throws GLException
    {
        if (info.renderMode == RenderMode.TRANSLUCENT) return;

        float s = cubeSize / 2f;
        GL2 gl = info.drawable.getGL().getGL2();

        if (info.renderMode != RenderMode.PICKING)
        {
            for (int i = 0; i < 8; i++)
            {
                gl.glActiveTexture(gl.GL_TEXTURE0 + i);
                gl.glDisable(gl.GL_TEXTURE_2D);
            }

            gl.glDepthFunc(gl.GL_LEQUAL);
            gl.glDepthMask(true);
            gl.glColor4f(fillColor.R, fillColor.G, fillColor.B, fillColor.A);
            gl.glDisable(gl.GL_LIGHTING);
            gl.glDisable(gl.GL_BLEND);
            gl.glDisable(gl.GL_COLOR_LOGIC_OP);
            gl.glDisable(gl.GL_ALPHA_TEST);
            gl.glCullFace(gl.GL_FRONT);
            try { gl.glUseProgram(0); } catch (GLException ex) { }
        }

        gl.glBegin(gl.GL_TRIANGLE_STRIP);
        gl.glVertex3f(-s, -s, -s);
        gl.glVertex3f(-s, s, -s);
        gl.glVertex3f(s, -s, -s);
        gl.glVertex3f(s, s, -s);
        gl.glVertex3f(s, -s, s);
        gl.glVertex3f(s, s, s);
        gl.glVertex3f(-s, -s, s);
        gl.glVertex3f(-s, s, s);
        gl.glVertex3f(-s, -s, -s);
        gl.glVertex3f(-s, s, -s);
        gl.glEnd();

        gl.glBegin(gl.GL_TRIANGLE_STRIP);
        gl.glVertex3f(-s, s, -s);
        gl.glVertex3f(-s, s, s);
        gl.glVertex3f(s, s, -s);
        gl.glVertex3f(s, s, s);
        gl.glEnd();

        gl.glBegin(gl.GL_TRIANGLE_STRIP);
        gl.glVertex3f(-s, -s, -s);
        gl.glVertex3f(s, -s, -s);
        gl.glVertex3f(-s, -s, s);
        gl.glVertex3f(s, -s, s);
        gl.glEnd();

        if (info.renderMode != RenderMode.PICKING)
        {
            gl.glLineWidth(1.5f);
            gl.glColor4f(borderColor.R, borderColor.G, borderColor.B, borderColor.A);

            gl.glBegin(gl.GL_LINE_STRIP);
            gl.glVertex3f(s, s, s);
            gl.glVertex3f(-s, s, s);
            gl.glVertex3f(-s, s, -s);
            gl.glVertex3f(s, s, -s);
            gl.glVertex3f(s, s, s);
            gl.glVertex3f(s, -s, s);
            gl.glVertex3f(-s, -s, s);
            gl.glVertex3f(-s, -s, -s);
            gl.glVertex3f(s, -s, -s);
            gl.glVertex3f(s, -s, s);
            gl.glEnd();

            gl.glBegin(gl.GL_LINES);
            gl.glVertex3f(-s, s, s);
            gl.glVertex3f(-s, -s, s);
            gl.glVertex3f(-s, s, -s);
            gl.glVertex3f(-s, -s, -s);
            gl.glVertex3f(s, s, -s);
            gl.glVertex3f(s, -s, -s);
            gl.glEnd();

            if (showAxes)
            {
                gl.glBegin(gl.GL_LINES);
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                gl.glVertex3f(0.0f, 0.0f, 0.0f);
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                gl.glVertex3f(s * 2.0f, 0.0f, 0.0f);
                gl.glColor3f(0.0f, 1.0f, 0.0f);
                gl.glVertex3f(0.0f, 0.0f, 0.0f);
                gl.glColor3f(0.0f, 1.0f, 0.0f);
                gl.glVertex3f(0.0f, s * 2.0f, 0.0f);
                gl.glColor3f(0.0f, 0.0f, 1.0f);
                gl.glVertex3f(0.0f, 0.0f, 0.0f);
                gl.glColor3f(0.0f, 0.0f, 1.0f);
                gl.glVertex3f(0.0f, 0.0f, s * 2.0f);
                gl.glEnd();
            }
        }
    }


    private float cubeSize;
    private Color4 borderColor, fillColor;
    private Boolean showAxes;
}
