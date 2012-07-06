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
    public partial class SettingsForm : Form
    {
        public SettingsForm()
        {
            InitializeComponent();
        }

        private void SettingsForm_Load(object sender, EventArgs e)
        {
            cbShaders.Checked = Properties.Settings.Default.UseShaders;
            cbPerformanceMode.Checked = Properties.Settings.Default.PerformanceMode;
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            Properties.Settings.Default.UseShaders = cbShaders.Checked;
            Properties.Settings.Default.PerformanceMode = cbPerformanceMode.Checked;
            Properties.Settings.Default.Save();
        }
    }
}
