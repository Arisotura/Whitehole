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

import java.nio.charset.Charset;
import java.util.prefs.Preferences;
import javax.media.opengl.GLProfile;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import whitehole.rendering.RendererCache;
import whitehole.rendering.ShaderCache;
import whitehole.rendering.TextureCache;
import whitehole.smg.Bcsv;
import whitehole.smg.GameArchive;

public class Whitehole 
{
    
    public static final String name = "Whitehole";
    public static final String version = "v1.2";
    public static String fullName = name + " " + version;
    //public static boolean isBeta = version.contains("beta");
    
    public static final String websiteURL = "http://kuribo64.net/";
    
    public static GameArchive game;
    
    
    public class UncaughtExceptionHandler
    {
        public void handle(Throwable throwable) 
        {
            System.out.println(throwable.getMessage());
        }
    }
    

    /**
     * @param args the command line arguments
     */
    public static void dorun()
    {
        if (!Charset.isSupported("SJIS"))
        {
            if (!Preferences.userRoot().getBoolean("charset-alreadyWarned", false))
            {
                JOptionPane.showMessageDialog(null, "Shift-JIS encoding isn't supported.\nWhitehole will default to ASCII, which may cause certain strings to look corrupted.\n\nThis message appears only once.", 
                        Whitehole.name, JOptionPane.WARNING_MESSAGE);
                Preferences.userRoot().putBoolean("charset-alreadyWarned", true);
            }
        }

        Settings.initialize();
        Bcsv.populateHashTable();
        ObjectDB.initialize();
        TextureCache.initialize();
        ShaderCache.initialize();
        RendererCache.initialize();

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
        }

        /*SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {*/
                GLProfile.initSingleton();
                new MainFrame().setVisible(true);
            /*}
        });*/
    }
    
    public static void main(String[] args) 
    {
        // attempt at catching uncaught exceptions
        // only half of them will go through the thing
        // the other half just causing the whole program to freeze forever
        // this shit sucks
        boolean catchemall = false;
        
        if (catchemall)
        {
            ThreadGroup strictgroup = new StrictThreadGroup();
            new Thread(strictgroup, "CATCH 'EM ALL")
            {
                public void run() 
                {
                    dorun();
                }
            }.start();
        }
        else
            dorun();
    }
}
