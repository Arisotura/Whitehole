namespace Whitehole
{
    partial class MainForm
    {
        /// <summary>
        /// Variable nécessaire au concepteur.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Nettoyage des ressources utilisées.
        /// </summary>
        /// <param name="disposing">true si les ressources managées doivent être supprimées ; sinon, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Code généré par le Concepteur Windows Form

        /// <summary>
        /// Méthode requise pour la prise en charge du concepteur - ne modifiez pas
        /// le contenu de cette méthode avec l'éditeur de code.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.tsTheToolbar = new System.Windows.Forms.ToolStrip();
            this.btnSelectFolder = new System.Windows.Forms.ToolStripButton();
            this.tsbDebugCrap = new System.Windows.Forms.ToolStripDropDownButton();
            this.btnHashCalc = new System.Windows.Forms.ToolStripMenuItem();
            this.btnBcsvEditor = new System.Windows.Forms.ToolStripMenuItem();
            this.btnObjNameTable = new System.Windows.Forms.ToolStripMenuItem();
            this.btnHelp = new System.Windows.Forms.ToolStripButton();
            this.btnSettings = new System.Windows.Forms.ToolStripButton();
            this.fbdGameFolder = new System.Windows.Forms.FolderBrowserDialog();
            this.lbLevelList = new System.Windows.Forms.ListBox();
            this.tsTheToolbar.SuspendLayout();
            this.SuspendLayout();
            // 
            // tsTheToolbar
            // 
            this.tsTheToolbar.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnSelectFolder,
            this.tsbDebugCrap,
            this.btnHelp,
            this.btnSettings});
            this.tsTheToolbar.Location = new System.Drawing.Point(0, 0);
            this.tsTheToolbar.Name = "tsTheToolbar";
            this.tsTheToolbar.Size = new System.Drawing.Size(533, 25);
            this.tsTheToolbar.TabIndex = 0;
            this.tsTheToolbar.Text = "toolStrip1";
            // 
            // btnSelectFolder
            // 
            this.btnSelectFolder.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnSelectFolder.Image = ((System.Drawing.Image)(resources.GetObject("btnSelectFolder.Image")));
            this.btnSelectFolder.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnSelectFolder.Name = "btnSelectFolder";
            this.btnSelectFolder.Size = new System.Drawing.Size(109, 22);
            this.btnSelectFolder.Text = "Select game folder";
            this.btnSelectFolder.ToolTipText = "Select the folder you extracted the game\'s files to.";
            this.btnSelectFolder.Click += new System.EventHandler(this.btnSelectFolder_Click);
            // 
            // tsbDebugCrap
            // 
            this.tsbDebugCrap.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
            this.tsbDebugCrap.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.tsbDebugCrap.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnHashCalc,
            this.btnBcsvEditor,
            this.btnObjNameTable});
            this.tsbDebugCrap.Image = ((System.Drawing.Image)(resources.GetObject("tsbDebugCrap.Image")));
            this.tsbDebugCrap.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.tsbDebugCrap.Name = "tsbDebugCrap";
            this.tsbDebugCrap.Size = new System.Drawing.Size(28, 22);
            this.tsbDebugCrap.Text = "D";
            this.tsbDebugCrap.ToolTipText = "debug crap";
            // 
            // btnHashCalc
            // 
            this.btnHashCalc.Name = "btnHashCalc";
            this.btnHashCalc.Size = new System.Drawing.Size(215, 22);
            this.btnHashCalc.Text = "Field name hash calculator";
            this.btnHashCalc.Click += new System.EventHandler(this.btnHashCalc_Click);
            // 
            // btnBcsvEditor
            // 
            this.btnBcsvEditor.Name = "btnBcsvEditor";
            this.btnBcsvEditor.Size = new System.Drawing.Size(215, 22);
            this.btnBcsvEditor.Text = "BCSV editor";
            this.btnBcsvEditor.Click += new System.EventHandler(this.btnBcsvEditor_Click);
            // 
            // btnObjNameTable
            // 
            this.btnObjNameTable.Name = "btnObjNameTable";
            this.btnObjNameTable.Size = new System.Drawing.Size(215, 22);
            this.btnObjNameTable.Text = "ObjNameTable";
            this.btnObjNameTable.Click += new System.EventHandler(this.btnObjNameTable_Click);
            // 
            // btnHelp
            // 
            this.btnHelp.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
            this.btnHelp.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnHelp.Image = ((System.Drawing.Image)(resources.GetObject("btnHelp.Image")));
            this.btnHelp.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnHelp.Name = "btnHelp";
            this.btnHelp.Size = new System.Drawing.Size(23, 22);
            this.btnHelp.Text = "?";
            this.btnHelp.Click += new System.EventHandler(this.btnHelp_Click);
            // 
            // btnSettings
            // 
            this.btnSettings.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
            this.btnSettings.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnSettings.Image = ((System.Drawing.Image)(resources.GetObject("btnSettings.Image")));
            this.btnSettings.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnSettings.Name = "btnSettings";
            this.btnSettings.Size = new System.Drawing.Size(53, 22);
            this.btnSettings.Text = "Settings";
            this.btnSettings.Click += new System.EventHandler(this.btnSettings_Click);
            // 
            // fbdGameFolder
            // 
            this.fbdGameFolder.ShowNewFolderButton = false;
            // 
            // lbLevelList
            // 
            this.lbLevelList.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lbLevelList.FormattingEnabled = true;
            this.lbLevelList.Location = new System.Drawing.Point(0, 25);
            this.lbLevelList.Name = "lbLevelList";
            this.lbLevelList.Size = new System.Drawing.Size(533, 368);
            this.lbLevelList.TabIndex = 1;
            this.lbLevelList.DoubleClick += new System.EventHandler(this.lbLevelList_DoubleClick);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(533, 395);
            this.Controls.Add(this.lbLevelList);
            this.Controls.Add(this.tsTheToolbar);
            this.Name = "MainForm";
            this.Text = "Whitehole - SMG1/2 editor";
            this.tsTheToolbar.ResumeLayout(false);
            this.tsTheToolbar.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ToolStrip tsTheToolbar;
        private System.Windows.Forms.FolderBrowserDialog fbdGameFolder;
        private System.Windows.Forms.ToolStripButton btnSelectFolder;
        private System.Windows.Forms.ToolStripDropDownButton tsbDebugCrap;
        private System.Windows.Forms.ToolStripMenuItem btnHashCalc;
        private System.Windows.Forms.ToolStripMenuItem btnBcsvEditor;
        private System.Windows.Forms.ToolStripMenuItem btnObjNameTable;
        private System.Windows.Forms.ToolStripButton btnHelp;
        private System.Windows.Forms.ToolStripButton btnSettings;
        private System.Windows.Forms.ListBox lbLevelList;
    }
}

