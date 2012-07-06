using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Whitehole
{
    public partial class BcsvEditorForm : Form
    {
        public BcsvEditorForm()
        {
            InitializeComponent();
        }

        private RarcFilesystem m_Archive = null;
        private Bcsv m_File = null;

        private void btnOpen_Click(object sender, EventArgs e)
        {
            RarcFilesystem arc;
            Bcsv file;

            if (m_Archive != null)
            {
                m_File.Close();
                m_Archive.Close();
            }

            //try
            {
                arc = new RarcFilesystem(Program.GameArchive.OpenFile(tbxArchive.Text));
                file = new Bcsv(arc.OpenFile(tbxFile.Text));
            }
           /* catch (Exception ex)
            {
                MessageBox.Show("Error opening file:\n\n" + ex.Message, "BCSV editor", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }*/

            m_Archive = arc;
            m_File = file;

            dgvBcsv.Rows.Clear();
            dgvBcsv.Columns.Clear();

            foreach (Bcsv.Field field in m_File.Fields.Values)
            {
                dgvBcsv.Columns.Add(field.NameHash.ToString("X8"), field.Name);
            }

            foreach (Bcsv.Entry entry in m_File.Entries)
            {
                object[] row = new object[entry.Count];
                int i = 0;

                foreach (KeyValuePair<uint, object> _val in entry)
                {
                    object val = _val.Value;
                    row[i++] = val;
                }

                dgvBcsv.Rows.Add(row);
            }

            btnSave.Enabled = true;
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            m_File.Entries.Clear();

            foreach (DataGridViewRow row in dgvBcsv.Rows)
            {
                if (row.IsNewRow) continue;

                Bcsv.Entry entry = new Bcsv.Entry();
                m_File.Entries.Add(entry);

                foreach (Bcsv.Field field in m_File.Fields.Values)
                {
                    uint hash = field.NameHash;
                    string valstring = row.Cells[hash.ToString("X8")].FormattedValue.ToString();

                    try
                    {
                        switch (field.Type)
                        {
                            case 0:
                            case 3: entry.Add(hash, uint.Parse(valstring)); break;
                            case 4: entry.Add(hash, ushort.Parse(valstring)); break;
                            case 5: entry.Add(hash, byte.Parse(valstring)); break;
                            case 2: entry.Add(hash, float.Parse(valstring)); break;
                            case 6: entry.Add(hash, valstring); break;
                        }
                    }
                    catch
                    {
                        switch (field.Type)
                        {
                            case 0:
                            case 3: entry.Add(hash, (uint)0); break;
                            case 4: entry.Add(hash, (ushort)0); break;
                            case 5: entry.Add(hash, (byte)0); break;
                            case 2: entry.Add(hash, 0f); break;
                            case 6: entry.Add(hash, ""); break;
                        }
                    }
                }
            }

            m_File.Flush();
        }

        private void BcsvEditorForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (m_Archive != null)
            {
                m_File.Close();
                m_Archive.Close();
            }
        }
    }
}
