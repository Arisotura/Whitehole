using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace Whitehole
{
    public enum SMGVersion
    {
        SMG1 = 1,
        SMG2
    }

    static class Program
    {
        public const string Name = "Whitehole";
        public const string Version = "v1.0 beta";
        public static bool IsBeta = Version.ToLower().Contains("beta");

        // Website URL -- this is shown in the About box, and to be used as a base URL for ObjectDB downloads
        public const string WebsiteURL = "http://kuribo64.cjb.net/";

        public static FilesystemBase GameArchive = null;
        public static SMGVersion GameVersion;

        /// <summary>
        /// Point d'entrée principal de l'application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            ModelCache.Init();
            Bcsv.PopulateHashtable();

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new MainForm());
        }
    }
}
