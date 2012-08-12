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

import whitehole.smg.*;
import javax.swing.*;
import java.nio.charset.Charset;
import java.util.prefs.Preferences;
import javax.media.opengl.GLProfile;

public class Whitehole 
{
    
    public static final String name = "Whitehole";
    public static final String version = "v1.0 beta";
    public static String fullName = name + " " + version;
    public static Boolean isBeta = version.contains("beta");
    
    public static final String websiteURL = "http://kuribo64.cjb.net/";
    
    public static GameArchive game;
    
    // settings
    public static Boolean objectDBUpdate;
    public static Boolean useShaders;
    public static Boolean fastDrag;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // TODO consider a fallback? (use ASCII for BCSV contents, and Japanese text will appear garbled)
        if (!Charset.isSupported("SJIS"))
        {
            JOptionPane.showMessageDialog(null, "Shift-JIS encoding isn't supported. Whitehole needs it.", Whitehole.name, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Preferences prefs = Preferences.userRoot();
        objectDBUpdate = prefs.getBoolean("ObjectDBUpdate", true);
        useShaders = prefs.getBoolean("UseShaders", true);
        fastDrag = prefs.getBoolean("FastDrag", false);
        
        Bcsv.populateHashTable();
        ObjectDB.initialize();
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
        }

        GLProfile.initSingleton();
        new MainFrame().setVisible(true);
    }
}
