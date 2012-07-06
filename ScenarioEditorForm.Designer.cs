namespace Whitehole
{
    partial class ScenarioEditorForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ScenarioEditorForm));
            this.scMainContainer = new System.Windows.Forms.SplitContainer();
            this.scLeftPanel = new System.Windows.Forms.SplitContainer();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.lbZoneList = new System.Windows.Forms.ListBox();
            this.btnAddZone = new System.Windows.Forms.ToolStripButton();
            this.btnDelZone = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.btnEditZone = new System.Windows.Forms.ToolStripButton();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.scMainContainer.Panel1.SuspendLayout();
            this.scMainContainer.Panel2.SuspendLayout();
            this.scMainContainer.SuspendLayout();
            this.scLeftPanel.Panel1.SuspendLayout();
            this.scLeftPanel.Panel2.SuspendLayout();
            this.scLeftPanel.SuspendLayout();
            this.toolStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // scMainContainer
            // 
            this.scMainContainer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.scMainContainer.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.scMainContainer.Location = new System.Drawing.Point(0, 0);
            this.scMainContainer.Name = "scMainContainer";
            // 
            // scMainContainer.Panel1
            // 
            this.scMainContainer.Panel1.Controls.Add(this.scLeftPanel);
            // 
            // scMainContainer.Panel2
            // 
            this.scMainContainer.Panel2.Controls.Add(this.label2);
            this.scMainContainer.Size = new System.Drawing.Size(789, 543);
            this.scMainContainer.SplitterDistance = 264;
            this.scMainContainer.TabIndex = 0;
            // 
            // scLeftPanel
            // 
            this.scLeftPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.scLeftPanel.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.scLeftPanel.Location = new System.Drawing.Point(0, 0);
            this.scLeftPanel.Name = "scLeftPanel";
            this.scLeftPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // scLeftPanel.Panel1
            // 
            this.scLeftPanel.Panel1.Controls.Add(this.lbZoneList);
            this.scLeftPanel.Panel1.Controls.Add(this.toolStrip1);
            // 
            // scLeftPanel.Panel2
            // 
            this.scLeftPanel.Panel2.Controls.Add(this.label1);
            this.scLeftPanel.Size = new System.Drawing.Size(264, 543);
            this.scLeftPanel.SplitterDistance = 194;
            this.scLeftPanel.TabIndex = 0;
            // 
            // toolStrip1
            // 
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.btnAddZone,
            this.btnDelZone,
            this.toolStripSeparator1,
            this.btnEditZone});
            this.toolStrip1.Location = new System.Drawing.Point(0, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(264, 25);
            this.toolStrip1.TabIndex = 0;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // lbZoneList
            // 
            this.lbZoneList.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lbZoneList.FormattingEnabled = true;
            this.lbZoneList.IntegralHeight = false;
            this.lbZoneList.Location = new System.Drawing.Point(0, 25);
            this.lbZoneList.Name = "lbZoneList";
            this.lbZoneList.Size = new System.Drawing.Size(264, 169);
            this.lbZoneList.TabIndex = 1;
            this.lbZoneList.SelectedIndexChanged += new System.EventHandler(this.lbZoneList_SelectedIndexChanged);
            this.lbZoneList.DoubleClick += new System.EventHandler(this.lbZoneList_DoubleClick);
            // 
            // btnAddZone
            // 
            this.btnAddZone.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnAddZone.Image = ((System.Drawing.Image)(resources.GetObject("btnAddZone.Image")));
            this.btnAddZone.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnAddZone.Name = "btnAddZone";
            this.btnAddZone.Size = new System.Drawing.Size(61, 22);
            this.btnAddZone.Text = "Add zone";
            // 
            // btnDelZone
            // 
            this.btnDelZone.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnDelZone.Enabled = false;
            this.btnDelZone.Image = ((System.Drawing.Image)(resources.GetObject("btnDelZone.Image")));
            this.btnDelZone.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnDelZone.Name = "btnDelZone";
            this.btnDelZone.Size = new System.Drawing.Size(82, 22);
            this.btnDelZone.Text = "Remove zone";
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
            // 
            // btnEditZone
            // 
            this.btnEditZone.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.btnEditZone.Enabled = false;
            this.btnEditZone.Image = ((System.Drawing.Image)(resources.GetObject("btnEditZone.Image")));
            this.btnEditZone.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.btnEditZone.Name = "btnEditZone";
            this.btnEditZone.Size = new System.Drawing.Size(59, 22);
            this.btnEditZone.Text = "Edit zone";
            this.btnEditZone.Click += new System.EventHandler(this.btnEditZone_Click);
            // 
            // label1
            // 
            this.label1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.label1.Location = new System.Drawing.Point(0, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(264, 345);
            this.label1.TabIndex = 0;
            this.label1.Text = "This space will be used in later versions of Whitehole. In the meantime, enjoy.";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // label2
            // 
            this.label2.Dock = System.Windows.Forms.DockStyle.Fill;
            this.label2.Location = new System.Drawing.Point(0, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(521, 543);
            this.label2.TabIndex = 1;
            this.label2.Text = "This space will be used in later versions of Whitehole. In the meantime, enjoy.";
            this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // ScenarioEditorForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(789, 543);
            this.Controls.Add(this.scMainContainer);
            this.Name = "ScenarioEditorForm";
            this.Text = "ScenarioEditorForm";
            this.Load += new System.EventHandler(this.ScenarioEditorForm_Load);
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.ScenarioEditorForm_FormClosing);
            this.scMainContainer.Panel1.ResumeLayout(false);
            this.scMainContainer.Panel2.ResumeLayout(false);
            this.scMainContainer.ResumeLayout(false);
            this.scLeftPanel.Panel1.ResumeLayout(false);
            this.scLeftPanel.Panel1.PerformLayout();
            this.scLeftPanel.Panel2.ResumeLayout(false);
            this.scLeftPanel.ResumeLayout(false);
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.SplitContainer scMainContainer;
        private System.Windows.Forms.SplitContainer scLeftPanel;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ListBox lbZoneList;
        private System.Windows.Forms.ToolStripButton btnAddZone;
        private System.Windows.Forms.ToolStripButton btnDelZone;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripButton btnEditZone;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;


    }
}