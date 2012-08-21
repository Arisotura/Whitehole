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

package whitehole;

import java.util.Enumeration;
import java.util.Iterator;

public class Iterator2Enumeration implements Enumeration
{
    public Iterator2Enumeration(Iterator it)
    {
        iterator = it;
    }
    
    @Override
    public boolean hasMoreElements()
    {
        return iterator.hasNext();
    }

    @Override
    public Object nextElement()
    {
        return iterator.next();
    }
    
    
    private Iterator iterator;
}
