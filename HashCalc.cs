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
    public partial class HashCalc : Form
    {
        public HashCalc()
        {
            InitializeComponent();
        }

        private void tbxString_TextChanged(object sender, EventArgs e)
        {
            tbxHash.Text = Bcsv.FieldNameToHash(tbxString.Text).ToString("X8");
        }
    }
}
