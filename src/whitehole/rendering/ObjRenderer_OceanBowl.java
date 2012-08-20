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
import javax.media.opengl.*;

// TODO turn this into general ScaledBmdRenderer/TransformedBmdRenderer?
public class ObjRenderer_OceanBowl extends BmdRenderer
{
    public ObjRenderer_OceanBowl(RenderInfo info) throws IOException
    {
        super(info, "WaterBowlObject");
    }
    
    @Override
    public void close(RenderInfo info) throws GLException
    {
        super.close(info);
    }
    
    
    @Override
    public void render(RenderInfo info) throws GLException
    {
        float factor = 15f;
        
        GL2 gl = info.drawable.getGL().getGL2();
        gl.glScalef(1f/factor, 1f/factor, 1f/factor);
        super.render(info);
    }
}
