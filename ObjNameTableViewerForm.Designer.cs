namespace Whitehole
{
    partial class ObjNameTableViewerForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ObjNameTableViewerForm));
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.btnTranslate = new System.Windows.Forms.ToolStripButton();
            this.pbTranslateProgress = new System.Windows.Forms.ToolStripProgressBar();
            this.btnCancel = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.btnTranslateOne = new System.Windows.Forms.ToolStripButton();
            this.lvObjNames = new System.Windows.Forms.ListView();
            this.columnHeader1 = new System.Windows.Forms.ColumnHeader();
            this.columnHeader2 = new System.Windows.Forms.ColumnHeader();
            this.columnHeader3 = new System.Windows.Forms.ColumnHeader();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.btnOneTimeUse = new System.Windows.Forms.ToolStripButton();
            this.toolStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // toolStrip1
            // 
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnTranslate,
            this.pbTranslateProgress,
            this.btnCancel,
            this.toolStripSeparator1,
            this.btnTranslateOne,
            this.toolStripSeparator2,
            this.btnOneTimeUse});
            this.toolStrip1.Location = new System.Drawing.Point(0, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(784, 25);
            this.toolStrip1.TabIndex = 0;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // btnTranslate
            // 
            this.btnTranslate.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnTranslate.Image = ((System.Drawing.Image)(resources.GetObject("btnTranslate.Image")));
            this.btnTranslate.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnTranslate.Name = "btnTranslate";
            this.btnTranslate.Size = new System.Drawing.Size(126, 22);
            this.btnTranslate.Text = "Translate descriptions";
            this.btnTranslate.ToolTipText = "Translates the descriptions from Japanese. May take a while to complete.";
            this.btnTranslate.Click += new System.EventHandler(this.btnTranslate_Click);
            // 
            // pbTranslateProgress
            // 
            this.pbTranslateProgress.Name = "pbTranslateProgress";
            this.pbTranslateProgress.Size = new System.Drawing.Size(100, 22);
            this.pbTranslateProgress.Visible = false;
            // 
            // btnCancel
            // 
            this.btnCancel.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnCancel.Image = ((System.Drawing.Image)(resources.GetObject("btnCancel.Image")));
            this.btnCancel.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(47, 22);
            this.btnCancel.Text = "Cancel";
            this.btnCancel.Visible = false;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
            // 
            // btnTranslateOne
            // 
            this.btnTranslateOne.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnTranslateOne.Image = ((System.Drawing.Image)(resources.GetObject("btnTranslateOne.Image")));
            this.btnTranslateOne.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnTranslateOne.Name = "btnTranslateOne";
            this.btnTranslateOne.Size = new System.Drawing.Size(105, 22);
            this.btnTranslateOne.Text = "Translate selected";
            this.btnTranslateOne.Click += new System.EventHandler(this.btnTranslateOne_Click);
            // 
            // lvObjNames
            // 
            this.lvObjNames.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1,
            this.columnHeader2,
            this.columnHeader3});
            this.lvObjNames.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lvObjNames.Location = new System.Drawing.Point(0, 25);
            this.lvObjNames.Name = "lvObjNames";
            this.lvObjNames.Size = new System.Drawing.Size(784, 474);
            this.lvObjNames.TabIndex = 1;
            this.lvObjNames.UseCompatibleStateImageBehavior = false;
            this.lvObjNames.View = System.Windows.Forms.View.Details;
            // 
            // columnHeader1
            // 
            this.columnHeader1.Text = "Name";
            this.columnHeader1.Width = 150;
            // 
            // columnHeader2
            // 
            this.columnHeader2.Text = "Description";
            this.columnHeader2.Width = 300;
            // 
            // columnHeader3
            // 
            this.columnHeader3.Text = "Translated description";
            this.columnHeader3.Width = 300;
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
            // 
            // btnOneTimeUse
            // 
            this.btnOneTimeUse.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnOneTimeUse.Image = ((System.Drawing.Image)(resources.GetObject("btnOneTimeUse.Image")));
            this.btnOneTimeUse.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnOneTimeUse.Name = "btnOneTimeUse";
            this.btnOneTimeUse.Size = new System.Drawing.Size(81, 22);
            this.btnOneTimeUse.Text = "one-time use";
            this.btnOneTimeUse.Click += new System.EventHandler(this.btnOneTimeUse_Click);
            // 
            // ObjNameTableViewerForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(784, 499);
            this.Controls.Add(this.lvObjNames);
            this.Controls.Add(this.toolStrip1);
            this.Name = "ObjNameTableViewerForm";
            this.Text = "ObjNameTable viewer (thank you Nintendo :3 )";
            this.Load += new System.EventHandler(this.ObjNameTableViewerForm_Load);
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.ObjNameTableViewerForm_FormClosing);
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton btnTranslate;
        private System.Windows.Forms.ListView lvObjNames;
        private System.Windows.Forms.ColumnHeader columnHeader1;
        private System.Windows.Forms.ColumnHeader columnHeader2;
        private System.Windows.Forms.ColumnHeader columnHeader3;
        private System.Windows.Forms.ToolStripProgressBar pbTranslateProgress;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripButton btnTranslateOne;
        private System.Windows.Forms.ToolStripButton btnCancel;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripButton btnOneTimeUse;
    }
}