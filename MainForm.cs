using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using OpenTK;
using OpenTK.Graphics.OpenGL;

namespace Whitehole
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }


        private void btnSelectFolder_Click(object sender, EventArgs e)
        {
            fbdGameFolder.SelectedPath = Properties.Settings.Default.LastDirectory;
            if (fbdGameFolder.ShowDialog(this) != DialogResult.OK)
                return;

            lbLevelList.Items.Clear();
            
           // try
            {
                // TODO: (low priority feature) add support for opening ISOs directly
                // * ISOs have partitions encrypted
                // * the filesystem isn't documented well
                Program.GameArchive = new ExternalFilesystem(fbdGameFolder.SelectedPath);

                // if the archive is a usable SMG1/2 archive, it will have a StageData directory
                // in the best case scenario there will also be ObjectData, but not needed
                if (!Program.GameArchive.DirectoryExists("/StageData"))
                    throw new Exception("Not a proper SMG1/2 archive: cannot find StageData directory");

                // list the levels available
                // nothing fancy, we just take the directories we got
                // for each directory name we have:
                // * /StageData/<name>/<name>Scenario.arc -> scenariodata and zonelist (tells which archives from StageData will be used)
                // * /stageData/<name>.arc -> defines the placement of the planets for each zone (in Placement/StageObjInfo)
                string[] leveldirs = Program.GameArchive.GetDirectories("/StageData");
                foreach (string level in leveldirs)
                {
                    string scenario_filename = string.Format("/StageData/{0}/{0}Scenario.arc", level);
                    RarcFilesystem scenario_arc = new RarcFilesystem(Program.GameArchive.OpenFile(scenario_filename));
                    Bcsv zonelist = new Bcsv(scenario_arc.OpenFile("/" + level + "Scenario/ZoneList.bcsv"));

                    // TODO: remove that
                    foreach (Bcsv.Entry entry in zonelist.Entries)
                    {
                        string zonename = (string)entry["ZoneName"];
                        //lvlnode.Nodes.Add(zonename).Tag = "Z|" + zonename;

                        // add the zonename to the Bcsv field name hashes list
                        // ScenarioData.bcsv uses zone names as field names
                        Bcsv.AddHash(zonename);
                    }

                   // lvlnode.Expand();
                    lbLevelList.Items.Add(level);

                    zonelist.Close();
                    scenario_arc.Close();
                }
                
                // remember the latest directory used
                Properties.Settings.Default.LastDirectory = fbdGameFolder.SelectedPath;
                Properties.Settings.Default.Save();
            }
            /*catch (Exception ex)
            {
                MessageBox.Show("An error has occured while trying to select this directory:\n\n" + ex.Message,
                    "Whitehole - Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }*/
        }

        private void btnHashCalc_Click(object sender, EventArgs e)
        {
            new HashCalc().Show(this);
        }

        private void lbLevelList_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
        {
            char action = ((string)e.Node.Tag)[0];
            string arg = ((string)e.Node.Tag).Substring(2);

            if (action == 'S')
            {
                new ScenarioEditorForm(arg).Show(this);
            }
            else if (action == 'Z')
            {
                new LevelEditorForm(arg).Show(this);
            }
        }

        private void btnBcsvEditor_Click(object sender, EventArgs e)
        {
            if (Program.GameArchive == null) return;
            new BcsvEditorForm().Show(this);
        }

        private void btnObjNameTable_Click(object sender, EventArgs e)
        {
            if (Program.GameArchive == null) return;
            new ObjNameTableViewerForm().Show(this);
        }

        private void btnHelp_Click(object sender, EventArgs e)
        {
            string msg =
                Program.Name + " " + Program.Version + "\n" +
                "\n" +
                "A level editor for Super Mario Galaxy 1 and 2\n" +
                "\n" +
                Program.Name + " is free software, and shouldn't be provided as\n" +
                "a part of a paid software package\n" + 
                "\n" +
                "Main coding: Mega-Mario\n" +
                "Credits: Phantom Wings, Treeki, yaz0r, thakis, groepaz/hitmen\n" + 
                "\n" + 
                "See " + Program.WebsiteURL + " for more details.\n";

            if (Program.Version.ToLower().Contains("private"))
                msg += "\nThis is a private beta version. Leak it out and this'll be the last one you get.\n";
            else if (Program.Version.ToLower().Contains("beta"))
                msg += "\nThis is a beta version so don't expect full stability.\n";

            MessageBox.Show(msg, "About " + Program.Name, MessageBoxButtons.OK, MessageBoxIcon.Information);
        }

        private void btnSettings_Click(object sender, EventArgs e)
        {
            new SettingsForm().ShowDialog(this);
        }

        private void lbLevelList_DoubleClick(object sender, EventArgs e)
        {
            new ScenarioEditorForm((string)lbLevelList.SelectedItem).Show(this);
        }
    }
}
