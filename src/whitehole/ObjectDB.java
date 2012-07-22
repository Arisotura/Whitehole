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
        Fallback = true;
        Timestamp = 0;
        
        Categories = new HashMap<>();
        Objects = new HashMap<>();
        
        File odbfile = new File("objectdb.xml");
        if (!odbfile.exists()) return;
        
        try
        {
            SAXBuilder sxb = new SAXBuilder();
            Document doc = sxb.build(odbfile);
            Element root = doc.getRootElement();
            Timestamp = root.getAttribute("timestamp").getLongValue();

            List<Element> categories = root.getChild("categories").getChildren("category");
            for (Element category : categories)
                Categories.put(category.getAttribute("id").getIntValue(), category.getText());
            
            List<Element> objects = root.getChildren("object");
            for (Element object : objects)
            {
                Object entry = new Object();
                entry.ID = object.getAttributeValue("id");
                
                entry.Name = object.getChildText("name");
                
                Objects.put(entry.ID, entry);
            }
        }
        catch (Exception ex)
        {
            Timestamp = 0;
            return;
        }
                
        Fallback = false;
    }
    
    
    public static class Object
    {
        public String ID;
        public String Name;
        // and so on
    }
    
    
    public static Boolean Fallback;
    public static long Timestamp;
    public static HashMap<Integer, String> Categories;
    public static HashMap<String, Object> Objects;
}
