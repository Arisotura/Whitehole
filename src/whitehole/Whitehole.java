package whitehole;

import javax.swing.*;
import java.nio.charset.Charset;

public class Whitehole 
{
    
    public static final String Name = "Whitehole";
    public static final String Version = "v1.0 beta";
    public static String FullName = Name + " " + Version;
    public static Boolean IsBeta = Version.contains("beta");
    
    public static final String WebsiteURL = "http://kuribo64.cjb.net/";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        if (!Charset.isSupported("SJIS"))
        {
            JOptionPane.showMessageDialog(null, "Shift-JIS encoding isn't supported. Whitehole needs it.", Whitehole.Name, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
        }

        new MainFrame().setVisible(true);
    }
}
