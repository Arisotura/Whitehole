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

import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;

public class ObjectDB 
{
    public static void Initialize()
    {
        fallback = true;
        timestamp = 0;
        
        categories = new HashMap<>();
        objects = new HashMap<>();
        
        File odbfile = new File("objectdb.xml");
        if (!odbfile.exists()) return;
        
        try
        {
            SAXBuilder sxb = new SAXBuilder();
            Document doc = sxb.build(odbfile);
            Element root = doc.getRootElement();
            timestamp = root.getAttribute("timestamp").getLongValue();

            List<Element> catelems = root.getChild("categories").getChildren("category");
            for (Element catelem : catelems)
                categories.put(catelem.getAttribute("id").getIntValue(), catelem.getText());
            
            List<Element> objelems = root.getChildren("object");
            for (Element objelem : objelems)
            {
                Object entry = new Object();
                entry.ID = objelem.getAttributeValue("id");
                
                entry.name = objelem.getChildText("name");
                
                objects.put(entry.ID, entry);
            }
        }
        catch (Exception ex)
        {
            timestamp = 0;
            return;
        }
                
        fallback = false;
    }
    
    
    public static class Object
    {
        public String ID;
        public String name;
        // and so on
    }
    
    
    public static Boolean fallback;
    public static long timestamp;
    public static HashMap<Integer, String> categories;
    public static HashMap<String, Object> objects;
}
