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

import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;

public class ObjectDB 
{
    public static void initialize()
    {
        fallback = true;
        timestamp = 0;
        
        categories = new LinkedHashMap<>();
        objects = new LinkedHashMap<>();
        
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
                
                Element flags = objelem.getChild("flags");
                entry.games = flags.getAttribute("games").getIntValue();
                
                entry.category = objelem.getChild("category").getAttribute("id").getIntValue();
                
                entry.preferredFile = objelem.getChild("preferredfile").getAttributeValue("name");
                entry.notes = objelem.getChildText("notes");
                
                entry.dataFiles = new ArrayList<>();
                String datafiles = objelem.getChildText("files");
                for (String datafile : datafiles.split("\n"))
                    entry.dataFiles.add(datafile);
                
                List<Element> fields = objelem.getChildren("field");
                entry.fields = new HashMap<>(fields.size());
                for (Element field : fields)
                {
                    Object.Field fielddata = new Object.Field();
                    
                    fielddata.ID = field.getAttribute("id").getIntValue();
                    fielddata.type = field.getAttributeValue("type");
                    fielddata.name = field.getAttributeValue("name");
                    fielddata.values = field.getAttributeValue("values");
                    fielddata.notes = field.getAttributeValue("notes");
                    
                    entry.fields.put(fielddata.ID, fielddata);
                }
                
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
        public static class Field
        {
            public int ID;
            
            public String type;
            public String name;
            public String values;
            public String notes;
        }
        
        
        public String ID;
        public String name;
        
        // bit0=SMG1, bit1=SMG2
        public int games;
        
        public int category;
        
        public String preferredFile;
        public String notes;
        public List<String> dataFiles;
        
        public HashMap<Integer, Field> fields;
    }
    
    
    public static boolean fallback;
    public static long timestamp;
    public static LinkedHashMap<Integer, String> categories;
    public static LinkedHashMap<String, Object> objects;
}
