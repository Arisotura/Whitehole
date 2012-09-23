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
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import whitehole.vectors.Vector3;

public class DoubleBmdRenderer extends GLRenderer
{
    public DoubleBmdRenderer(RenderInfo info, String model1, Vector3 pos1, String model2, Vector3 pos2) throws IOException
    {
        rend1 = new BmdRenderer(info, model1);
        position1 = pos1;
        rend2 = new BmdRenderer(info, model2);
        position2 = pos2;
    }
    
    @Override
    public void close(RenderInfo info) throws GLException
    {
        rend1.close(info);
        rend2.close(info);
    }
    
    
    @Override
    public boolean gottaRender(RenderInfo info) throws GLException
    {
        return rend1.gottaRender(info) || rend2.gottaRender(info);
    }
    
    @Override
    public void render(RenderInfo info) throws GLException
    {
        GL2 gl = info.drawable.getGL().getGL2();
        
        if (rend1.gottaRender(info))
        {
            gl.glTranslatef(position1.x, position1.y, position1.z);
            rend1.render(info);
        }
        if (rend2.gottaRender(info))
        {
            gl.glTranslatef(position2.x, position2.y, position2.z);
            rend2.render(info);
        }
    }
    
    
    private BmdRenderer rend1, rend2;
    private Vector3 position1, position2;
}
