namespace Whitehole
{
    partial class LevelEditorForm
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
            this.tsToolbar = new System.Windows.Forms.ToolStrip();
            this.scMainContainer = new System.Windows.Forms.SplitContainer();
            this.glLevelView = new OpenTK.GLControl();
            this.scLeftPanel = new System.Windows.Forms.SplitContainer();
            this.pgObjectProperties = new System.Windows.Forms.PropertyGrid();
            this.scMainContainer.Panel1.SuspendLayout();
            this.scMainContainer.Panel2.SuspendLayout();
            this.scMainContainer.SuspendLayout();
            this.scLeftPanel.Panel2.SuspendLayout();
            this.scLeftPanel.SuspendLayout();
            this.SuspendLayout();
            // 
            // tsToolbar
            // 
            this.tsToolbar.Location = new System.Drawing.Point(0, 0);
            this.tsToolbar.Name = "tsToolbar";
            this.tsToolbar.Size = new System.Drawing.Size(824, 25);
            this.tsToolbar.TabIndex = 0;
            this.tsToolbar.Text = "toolStrip1";
            // 
            // scMainContainer
            // 
            this.scMainContainer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.scMainContainer.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.scMainContainer.Location = new System.Drawing.Point(0, 25);
            this.scMainContainer.Name = "scMainContainer";
            // 
            // scMainContainer.Panel1
            // 
            this.scMainContainer.Panel1.Controls.Add(this.scLeftPanel);
            // 
            // scMainContainer.Panel2
            // 
            this.scMainContainer.Panel2.Controls.Add(this.glLevelView);
            this.scMainContainer.Size = new System.Drawing.Size(824, 498);
            this.scMainContainer.SplitterDistance = 261;
            this.scMainContainer.TabIndex = 1;
            // 
            // glLevelView
            // 
            this.glLevelView.BackColor = System.Drawing.Color.Black;
            this.glLevelView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.glLevelView.Location = new System.Drawing.Point(0, 0);
            this.glLevelView.Name = "glLevelView";
            this.glLevelView.Size = new System.Drawing.Size(559, 498);
            this.glLevelView.TabIndex = 0;
            this.glLevelView.VSync = false;
            this.glLevelView.Load += new System.EventHandler(this.glLevelView_Load);
            this.glLevelView.MouseWheel += new System.Windows.Forms.MouseEventHandler(this.glLevelView_MouseWheel);
            this.glLevelView.Paint += new System.Windows.Forms.PaintEventHandler(this.glLevelView_Paint);
            this.glLevelView.MouseMove += new System.Windows.Forms.MouseEventHandler(this.glLevelView_MouseMove);
            this.glLevelView.MouseDown += new System.Windows.Forms.MouseEventHandler(this.glLevelView_MouseDown);
            this.glLevelView.Resize += new System.EventHandler(this.glLevelView_Resize);
            this.glLevelView.MouseUp += new System.Windows.Forms.MouseEventHandler(this.glLevelView_MouseUp);
            // 
            // scLeftPanel
            // 
            this.scLeftPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.scLeftPanel.Location = new System.Drawing.Point(0, 0);
            this.scLeftPanel.Name = "scLeftPanel";
            this.scLeftPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // scLeftPanel.Panel2
            // 
            this.scLeftPanel.Panel2.Controls.Add(this.pgObjectProperties);
            this.scLeftPanel.Size = new System.Drawing.Size(261, 498);
            this.scLeftPanel.SplitterDistance = 244;
            this.scLeftPanel.TabIndex = 0;
            // 
            // pgObjectProperties
            // 
            this.pgObjectProperties.Dock = System.Windows.Forms.DockStyle.Fill;
            this.pgObjectProperties.Location = new System.Drawing.Point(0, 0);
            this.pgObjectProperties.Name = "pgObjectProperties";
            this.pgObjectProperties.PropertySort = System.Windows.Forms.PropertySort.Categorized;
            this.pgObjectProperties.Size = new System.Drawing.Size(261, 250);
            this.pgObjectProperties.TabIndex = 0;
            this.pgObjectProperties.ToolbarVisible = false;
            // 
            // LevelEditorForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(824, 523);
            this.Controls.Add(this.scMainContainer);
            this.Controls.Add(this.tsToolbar);
            this.Name = "LevelEditorForm";
            this.Text = "Whitehole the epic new SMG editor";
            this.Load += new System.EventHandler(this.LevelEditorForm_Load);
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.LevelEditorForm_FormClosed);
            this.scMainContainer.Panel1.ResumeLayout(false);
            this.scMainContainer.Panel2.ResumeLayout(false);
            this.scMainContainer.ResumeLayout(false);
            this.scLeftPanel.Panel2.ResumeLayout(false);
            this.scLeftPanel.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ToolStrip tsToolbar;
        private System.Windows.Forms.SplitContainer scMainContainer;
        private OpenTK.GLControl glLevelView;
        private System.Windows.Forms.SplitContainer scLeftPanel;
        private System.Windows.Forms.PropertyGrid pgObjectProperties;
    }
}