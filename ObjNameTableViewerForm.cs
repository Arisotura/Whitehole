using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace Whitehole
{
    public partial class ObjNameTableViewerForm : Form
    {
        public ObjNameTableViewerForm()
        {
            InitializeComponent();
        }

        private RarcFilesystem m_Rarc;
        private Bcsv m_List;
        private string[] m_Translations;
        private BackgroundWorker m_Lazyman;
        private bool m_Running, m_Cancel;

        private void ObjNameTableViewerForm_Load(object sender, EventArgs e)
        {
            m_Rarc = new RarcFilesystem(Program.GameArchive.OpenFile("/StageData/ObjNameTable.arc"));
            m_List = new Bcsv(m_Rarc.OpenFile("/ObjNameTable/ObjNameTable.tbl"));
            m_Translations = new string[m_List.Entries.Count];
            for (int i = 0; i < m_List.Entries.Count; i++)
                m_Translations[i] = "";

            // TODO: find out why that silly BackgroundWorker won't work twice
            m_Lazyman = new BackgroundWorker();
            m_Lazyman.WorkerReportsProgress = true;
            m_Lazyman.WorkerSupportsCancellation = true;
            m_Lazyman.DoWork += new DoWorkEventHandler(this.DoTranslate);
            m_Lazyman.ProgressChanged += new ProgressChangedEventHandler(this.TranslateProgressChanged);
            m_Lazyman.RunWorkerCompleted += new RunWorkerCompletedEventHandler(this.TranslateFinished);
            m_Running = m_Cancel = false;

            foreach (Bcsv.Entry entry in m_List.Entries)
            {
                // TODO: find the corresponding strings for those hashes? :P
                string objname = (string)entry[0x9FF8A861];
                string objdesc = (string)entry[0xABE181E4];

                ListViewItem item = new ListViewItem(objname);
                item.SubItems.Add(objdesc);
                item.SubItems.Add("");
                lvObjNames.Items.Add(item);
            }
        }

        private void DoTranslate(object sender, DoWorkEventArgs e)
        {
            m_Running = true;

            int i = 0;
            foreach (Bcsv.Entry entry in m_List.Entries)
            {
                if (m_Cancel) { m_Running = m_Cancel = false; return; }
                if (m_Translations[i] != "") continue;

                string objdesc = (string)entry[0xABE181E4];
                string translated = Translator.Translate(objdesc, "ja|en");

                m_Translations[i] = translated;
                m_Lazyman.ReportProgress(i);
                i++;
            }
        }

        private void TranslateProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            for (int i = pbTranslateProgress.Value; i < e.ProgressPercentage; i++)
                lvObjNames.Items[i].SubItems[2].Text = m_Translations[i];
            pbTranslateProgress.Value = e.ProgressPercentage;
        }

        private void TranslateFinished(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Error != null) throw e.Error;

            btnTranslate.Enabled = true;
            btnTranslate.Text = "Translate descriptions";
            pbTranslateProgress.Visible = false;
            btnCancel.Visible = false;
        }

        private void btnTranslate_Click(object sender, EventArgs e)
        {
            pbTranslateProgress.Minimum = 0;
            pbTranslateProgress.Maximum = m_List.Entries.Count;
            pbTranslateProgress.Step = 1;
            pbTranslateProgress.Value = 0;
            pbTranslateProgress.Visible = true;
            btnCancel.Visible = true;
            btnTranslate.Enabled = false;
            btnTranslate.Text = "Translating...";

            m_Cancel = false;
            m_Lazyman.RunWorkerAsync();
        }

        private void btnTranslateOne_Click(object sender, EventArgs e)
        {
            if (lvObjNames.SelectedItems.Count < 1) return;

            for (int j = 0; j < lvObjNames.SelectedIndices.Count; j++)
            {
                int i = lvObjNames.SelectedIndices[j];
                if (m_Translations[i] != "") continue;
                m_Translations[i] = Translator.Translate(lvObjNames.Items[i].SubItems[1].Text, "ja|en");
                lvObjNames.Items[i].SubItems[2].Text = m_Translations[i];
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
           // m_Cancel = true;
           // while (m_Running) { }
        }

        private void ObjNameTableViewerForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            //m_Cancel = true;
            //while (m_Running) { }
        }
    }
}
