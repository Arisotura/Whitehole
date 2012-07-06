using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Whitehole
{
    public partial class ScenarioEditorForm : Form
    {
        public ScenarioEditorForm(string level)
        {
            InitializeComponent();

            LevelName = level;
            m_ZoneEditors = new List<LevelEditorForm>();
        }


        public string LevelName;
        private RarcFilesystem m_ScenarioContainer;
        private Bcsv m_ZoneList, m_ScenarioData;

        private List<LevelEditorForm> m_ZoneEditors;


        private void ScenarioEditorForm_Load(object sender, EventArgs e)
        {
            Text = LevelName + " - " + Program.Name;

            m_ScenarioContainer = new RarcFilesystem(Program.GameArchive.OpenFile(string.Format("/StageData/{0}/{0}Scenario.arc", LevelName)));
            m_ZoneList = new Bcsv(m_ScenarioContainer.OpenFile("/" + LevelName + "Scenario/ZoneList.bcsv"));
            m_ScenarioData = new Bcsv(m_ScenarioContainer.OpenFile("/" + LevelName + "Scenario/ScenarioData.bcsv"));

            foreach (Bcsv.Entry entry in m_ZoneList.Entries)
            {
                lbZoneList.Items.Add((string)entry["ZoneName"]);
            }
        }

        private void ScenarioEditorForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            // TODO close confirm

            while (m_ZoneEditors.Count > 0)
                m_ZoneEditors[0].Close();

            m_ZoneList.Close();
            m_ScenarioData.Close();
            m_ScenarioContainer.Close();
        }

        private void lbZoneList_SelectedIndexChanged(object sender, EventArgs e)
        {
            bool enableactions = (lbZoneList.SelectedIndex > -1);
            btnDelZone.Enabled = enableactions;
            btnEditZone.Enabled = enableactions;
        }

        private void lbZoneList_DoubleClick(object sender, EventArgs e)
        {
            if (lbZoneList.SelectedIndex < 0) return;
            btnEditZone.PerformClick();
        }

        private void btnEditZone_Click(object sender, EventArgs e)
        {
            string zonename = (string)lbZoneList.SelectedItem;

            foreach (LevelEditorForm existing in m_ZoneEditors)
            {
                if (existing.ZoneName == zonename)
                {
                    existing.Focus();
                    return;
                }
            }

            LevelEditorForm editor = new LevelEditorForm(zonename);
            editor.FormClosed += new FormClosedEventHandler(OnCloseEditor);
            editor.Show(this);
            m_ZoneEditors.Add(editor);
        }

        void OnCloseEditor(object sender, FormClosedEventArgs e)
        {
            m_ZoneEditors.Remove((LevelEditorForm)sender);
        }
    }
}
