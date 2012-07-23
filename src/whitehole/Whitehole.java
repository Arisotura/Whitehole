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

import javax.swing.*;
import java.nio.charset.Charset;

public class Whitehole 
{
    
    public static final String Name = "Whitehole";
    public static final String Version = "v1.0 beta";
    public static String FullName = Name + " " + Version;
    public static Boolean IsBeta = Version.contains("beta");
    
    public static final String WebsiteURL = "http://kuribo64.cjb.net/";
    
    public static GameArchive Game;

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
        
        Bcsv.PopulateHashtable();
        ObjectDB.Initialize();
        
        
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
