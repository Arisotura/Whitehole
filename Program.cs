using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace Whitehole
{
    static class Program
    {
        public static string Name = "Whitehole";
        public static string Version = "v1.0 beta";
        public static bool IsBeta = Version.ToLower().Contains("beta");

        public static FilesystemBase GameArchive = null;

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
