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

import javax.media.opengl.*;

public interface GLRenderer 
{
    public void close(RenderInfo info) throws GLException;

    public Boolean gottaRender(RenderInfo info) throws GLException;
    public void render(RenderInfo info) throws GLException;
    
    
    public enum RenderMode
    {
        PICKING,
        OPAQUE,
        TRANSLUCENT
    }
    
    public class RenderInfo
    {
        public GLAutoDrawable drawable;
        public RenderMode renderMode;
    }
}
