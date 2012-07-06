namespace Whitehole
{
    partial class HashCalc
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
            this.label1 = new System.Windows.Forms.Label();
            this.tbxString = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.tbxHash = new System.Windows.Forms.TextBox();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(37, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "String:";
            // 
            // tbxString
            // 
            this.tbxString.Location = new System.Drawing.Point(55, 6);
            this.tbxString.Name = "tbxString";
            this.tbxString.Size = new System.Drawing.Size(269, 20);
            this.tbxString.TabIndex = 1;
            this.tbxString.TextChanged += new System.EventHandler(this.tbxString_TextChanged);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(14, 40);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(35, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "Hash:";
            // 
            // tbxHash
            // 
            this.tbxHash.Location = new System.Drawing.Point(55, 37);
            this.tbxHash.Name = "tbxHash";
            this.tbxHash.ReadOnly = true;
            this.tbxHash.Size = new System.Drawing.Size(100, 20);
            this.tbxHash.TabIndex = 3;
            // 
            // HashCalc
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(336, 66);
            this.Controls.Add(this.tbxHash);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.tbxString);
            this.Controls.Add(this.label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "HashCalc";
            this.Text = "Field name hash calculator";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox tbxString;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox tbxHash;
    }
}