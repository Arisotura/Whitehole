package whitehole;

import java.io.*;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.filter.*;

public class ObjectDB 
{
    public static void Initialize()
    {
        Fallback = true;
        Timestamp = 0;
        
        File odbfile = new File("objectdb.xml");
        if (!odbfile.exists()) return;
        
        try
        {
            SAXBuilder sxb = new SAXBuilder();
            Document doc = sxb.build(odbfile);
            Element root = doc.getRootElement();
            Timestamp = root.getAttribute("timestamp").getLongValue();
            System.out.println(Timestamp);
        }
        catch (Exception ex)
        {
            Timestamp = 0;
            return;
        }
                
        Fallback = false;
    }
    
    
    public static Boolean Fallback;
    public static long Timestamp;
}
