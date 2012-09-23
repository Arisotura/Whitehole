/*
    Copyright 2012 The Whitehole team

    This file is part of Whitehole.

    Whitehole is free software: you can redistribute it and/or modify it under
    the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    Whitehole is distributed in the hope that it will be useful, but WITHOUT ANY 
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
    FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along 
    with Whitehole. If not, see http://www.gnu.org/licenses/.
*/

package whitehole;

import java.io.*;
import java.nio.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Map.*;
import javax.swing.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.*;
import javax.swing.tree.*;
import whitehole.vectors.*;
import whitehole.rendering.*;
import whitehole.smg.*;

/**
 *
 * @author lolol
 */
public class GalaxyEditorForm extends javax.swing.JFrame
{

    /**
     * Creates new form GalaxyEditorForm
     */
    public GalaxyEditorForm(String galaxy)
    {
        initComponents();
        
        maxUniqueID = 0;
        globalObjList = new HashMap<>();
        subZoneData = new HashMap<>();

        galaxyMode = true;
        parentForm = null;
        childZoneEditors = new HashMap<>();
        galaxyName = galaxy;
        try
        {
            galaxyArc = Whitehole.game.openGalaxy(galaxyName);
            
            zoneArcs = new HashMap<>(galaxyArc.zoneList.size());
            for (String zone : galaxyArc.zoneList)
                loadZone(zone);
            
            ZoneArchive mainzone = zoneArcs.get(galaxyName);
            for (int i = 0; i < galaxyArc.scenarioData.size(); i++)
            {
                for (Bcsv.Entry subzone : mainzone.subZones.get("common"))
                {
                    SubZoneData data = new SubZoneData();
                    data.layer = "common";
                    data.position = new Vector3((float)subzone.get("pos_x"), (float)subzone.get("pos_y"), (float)subzone.get("pos_z"));
                    data.rotation = new Vector3((float)subzone.get("dir_x"), (float)subzone.get("dir_y"), (float)subzone.get("dir_z"));
                    
                    String key = String.format("%1$d/%2$s", i, (String)subzone.get("name"));
                    if (subZoneData.containsKey(key)) throw new IOException("Duplicate zone " + key);
                    subZoneData.put(key, data);
                }
                
                int mainlayermask = (int)galaxyArc.scenarioData.get(i).get(galaxyName);
                for (int l = 0; l < 32; l++)
                {
                    if ((mainlayermask & (1 << l)) == 0)
                        continue;
                    
                    String layer = "layer" + ('a'+l);
                    if (!mainzone.subZones.containsKey(layer))
                        continue;
                    
                    for (Bcsv.Entry subzone : mainzone.subZones.get(layer))
                    {
                        SubZoneData data = new SubZoneData();
                        data.layer = layer;
                        data.position = new Vector3((float)subzone.get("pos_x"), (float)subzone.get("pos_y"), (float)subzone.get("pos_z"));
                        data.rotation = new Vector3((float)subzone.get("dir_x"), (float)subzone.get("dir_y"), (float)subzone.get("dir_z"));

                        String key = String.format("%1$d/%2$s", i, (String)subzone.get("name"));
                        if (subZoneData.containsKey(key)) throw new IOException("Duplicate zone " + key);
                        subZoneData.put(key, data);
                    }
                }
            }
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, "Failed to open the galaxy: "+ex.getMessage(), Whitehole.name, JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initGUI();
        
        // hax
        btnAddScenario.setVisible(false);
        btnEditScenario.setVisible(false);
        btnDeleteScenario.setVisible(false);
        btnAddZone.setVisible(false);
        btnDeleteZone.setVisible(false);
        
        tpLeftPanel.remove(1);
    }
    
    public GalaxyEditorForm(GalaxyEditorForm gal_parent, ZoneArchive zone)
    {
        initComponents();
        
        maxUniqueID = 0;
        globalObjList = new HashMap<>();
        subZoneData = null;
        galaxyArc = null;

        galaxyMode = false;
        parentForm = gal_parent;
        childZoneEditors = null;
        galaxyName = zone.zoneName; // hax
        try
        {
            zoneArcs = new HashMap<>(1);
            zoneArcs.put(galaxyName, zone);
            loadZone(galaxyName);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, "Failed to open the zone: "+ex.getMessage(), Whitehole.name, JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        curZone = galaxyName;
        curZoneArc = zoneArcs.get(curZone);
        
        initGUI();
        
        tpLeftPanel.remove(0);
        
        lbLayersList = new CheckBoxList();
        lbLayersList.setEventListener(new CheckBoxList.EventListener()
        {
            public void checkBoxStatusChanged(int index, boolean status)
            { layerSelectChange(index, status); }
        });
        scpLayersList.setViewportView(lbLayersList);
        pack();
        
        zoneModeLayerBitmask = 1;
        JCheckBox[] cblayers = new JCheckBox[curZoneArc.objects.keySet().size()];
        int i = 0;
        cblayers[i] = new JCheckBox("Common");
        cblayers[i].setSelected(true);
        i++;
        for (int l = 0; l < 26; l++)
        {
            String ls = String.format("Layer%1$c", 'A'+l);
            if (curZoneArc.objects.containsKey(ls.toLowerCase()))
            {
                cblayers[i] = new JCheckBox(ls);
                if (i == 1) 
                {
                    cblayers[i].setSelected(true);
                    zoneModeLayerBitmask |= (2 << l);
                }
                i++;
            }
        }
        lbLayersList.setListData(cblayers);
        
        populateObjectList(zoneModeLayerBitmask);
    }
    
    private void initGUI()
    {
        setTitle(galaxyName + " - " + Whitehole.fullName);
        setIconImage(Toolkit.getDefaultToolkit().createImage(Whitehole.class.getResource("/Resources/icon.png")));

        glCanvas = new GLCanvas(null, null, RendererCache.refContext, null);
        glCanvas.addGLEventListener(renderer = new GalaxyRenderer());
        glCanvas.addMouseListener(renderer);
        glCanvas.addMouseMotionListener(renderer);
        glCanvas.addMouseWheelListener(renderer);
        
        pnlGLPanel.add(glCanvas, BorderLayout.CENTER);
        pnlGLPanel.validate();
        
        pnlObjectSettings = new PropertyPanel();
        scpObjSettingsContainer.setViewportView(pnlObjectSettings);
        scpObjSettingsContainer.getVerticalScrollBar().setUnitIncrement(16);
        //pnlObjectSettings.setEventListener(this);
        pnlObjectSettings.setEventListener(new PropertyPanel.EventListener() 
        {
            public void propertyChanged(String propname, Object value)
            { propPanelPropertyChanged(propname, value); }
        });
    }
    
    private void loadZone(String zone) throws IOException
    {
        ZoneArchive arc;
        if (galaxyMode) 
        {
            arc = galaxyArc.openZone(zone);
            zoneArcs.put(zone, arc);
        }
        else 
            arc = zoneArcs.get(zone);
        
        for (java.util.List<LevelObject> objlist : arc.objects.values())
        {
            for (LevelObject obj : objlist)
            {
                globalObjList.put(maxUniqueID, obj);
                obj.uniqueID = maxUniqueID;
                
                maxUniqueID++;
            }
        }
    }
    
    
    public void updateZone(String zone)
    {
        rerenderTasks.add("zone:"+zone);
        glCanvas.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlGLPanel = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        lbSelected = new javax.swing.JLabel();
        btnDeselect = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        lbStatusLabel = new javax.swing.JLabel();
        tpLeftPanel = new javax.swing.JTabbedPane();
        pnlScenarioZonePanel = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        btnAddScenario = new javax.swing.JButton();
        btnEditScenario = new javax.swing.JButton();
        btnDeleteScenario = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbScenarioList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        btnAddZone = new javax.swing.JButton();
        btnDeleteZone = new javax.swing.JButton();
        btnEditZone = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lbZoneList = new javax.swing.JList();
        pnlLayersPanel = new javax.swing.JPanel();
        jToolBar6 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        scpLayersList = new javax.swing.JScrollPane();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        jLabel5 = new javax.swing.JLabel();
        tgbAddObject = new javax.swing.JToggleButton();
        tgbDeleteObject = new javax.swing.JToggleButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tvObjectList = new javax.swing.JTree();
        scpObjSettingsContainer = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setFocusable(false);
        jSplitPane1.setLastDividerLocation(300);

        pnlGLPanel.setMinimumSize(new java.awt.Dimension(10, 30));
        pnlGLPanel.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        jLabel2.setText("Selected: ");
        jToolBar2.add(jLabel2);

        lbSelected.setText("none");
        jToolBar2.add(lbSelected);

        btnDeselect.setText("(deselect)");
        btnDeselect.setEnabled(false);
        btnDeselect.setFocusable(false);
        btnDeselect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeselect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDeselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeselectActionPerformed(evt);
            }
        });
        jToolBar2.add(btnDeselect);
        jToolBar2.add(jSeparator2);

        pnlGLPanel.add(jToolBar2, java.awt.BorderLayout.NORTH);

        lbStatusLabel.setText("status text goes here");
        pnlGLPanel.add(lbStatusLabel, java.awt.BorderLayout.PAGE_END);

        jSplitPane1.setRightComponent(pnlGLPanel);

        tpLeftPanel.setMinimumSize(new java.awt.Dimension(100, 5));

        pnlScenarioZonePanel.setDividerLocation(200);
        pnlScenarioZonePanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        pnlScenarioZonePanel.setLastDividerLocation(200);

        jPanel1.setPreferredSize(new java.awt.Dimension(201, 200));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        jLabel3.setText("Scenarios:");
        jToolBar3.add(jLabel3);

        btnAddScenario.setText("Add");
        btnAddScenario.setFocusable(false);
        btnAddScenario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddScenario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(btnAddScenario);

        btnEditScenario.setText("Edit");
        btnEditScenario.setFocusable(false);
        btnEditScenario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditScenario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(btnEditScenario);

        btnDeleteScenario.setText("Delete");
        btnDeleteScenario.setFocusable(false);
        btnDeleteScenario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteScenario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(btnDeleteScenario);

        jPanel1.add(jToolBar3, java.awt.BorderLayout.PAGE_START);

        lbScenarioList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lbScenarioList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lbScenarioListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lbScenarioList);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pnlScenarioZonePanel.setTopComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        jLabel4.setText("Zones:");
        jToolBar4.add(jLabel4);

        btnAddZone.setText("Add");
        btnAddZone.setFocusable(false);
        btnAddZone.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddZone.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(btnAddZone);

        btnDeleteZone.setText("Delete");
        btnDeleteZone.setFocusable(false);
        btnDeleteZone.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteZone.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(btnDeleteZone);

        btnEditZone.setText("Edit individually");
        btnEditZone.setFocusable(false);
        btnEditZone.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditZone.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditZone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditZoneActionPerformed(evt);
            }
        });
        jToolBar4.add(btnEditZone);

        jPanel2.add(jToolBar4, java.awt.BorderLayout.PAGE_START);

        lbZoneList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lbZoneList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lbZoneListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lbZoneList);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pnlScenarioZonePanel.setRightComponent(jPanel2);

        tpLeftPanel.addTab("Scenario/Zone", pnlScenarioZonePanel);

        pnlLayersPanel.setLayout(new java.awt.BorderLayout());

        jToolBar6.setFloatable(false);
        jToolBar6.setRollover(true);

        jLabel1.setText("Layers:");
        jToolBar6.add(jLabel1);

        pnlLayersPanel.add(jToolBar6, java.awt.BorderLayout.PAGE_START);
        pnlLayersPanel.add(scpLayersList, java.awt.BorderLayout.CENTER);

        tpLeftPanel.addTab("Layers", pnlLayersPanel);

        jSplitPane4.setDividerLocation(300);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setResizeWeight(0.5);
        jSplitPane4.setFocusCycleRoot(true);
        jSplitPane4.setLastDividerLocation(300);

        jPanel3.setPreferredSize(new java.awt.Dimension(149, 300));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);

        jLabel5.setText("Objects:");
        jToolBar5.add(jLabel5);

        tgbAddObject.setText("Add");
        tgbAddObject.setFocusable(false);
        tgbAddObject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tgbAddObject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tgbAddObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbAddObjectActionPerformed(evt);
            }
        });
        jToolBar5.add(tgbAddObject);

        tgbDeleteObject.setText("Delete");
        tgbDeleteObject.setFocusable(false);
        tgbDeleteObject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tgbDeleteObject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tgbDeleteObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbDeleteObjectActionPerformed(evt);
            }
        });
        jToolBar5.add(tgbDeleteObject);

        jPanel3.add(jToolBar5, java.awt.BorderLayout.PAGE_START);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tvObjectList.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tvObjectList.setShowsRootHandles(true);
        tvObjectList.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                tvObjectListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(tvObjectList);

        jPanel3.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jSplitPane4.setTopComponent(jPanel3);
        jSplitPane4.setRightComponent(scpObjSettingsContainer);

        tpLeftPanel.addTab("Objects", jSplitPane4);

        jSplitPane1.setLeftComponent(tpLeftPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowOpened
    {//GEN-HEADEREND:event_formWindowOpened
        if (galaxyMode)
        {
            DefaultListModel scenlist = new DefaultListModel();
            lbScenarioList.setModel(scenlist);
            for (Bcsv.Entry scen : galaxyArc.scenarioData)
            {
                scenlist.addElement(String.format("[%1$d] %2$s", (int)scen.get("ScenarioNo"), (String)scen.get("ScenarioName")));
            }

            lbScenarioList.setSelectedIndex(0);
        }
        
        //
    }//GEN-LAST:event_formWindowOpened

    public void selectionChanged()
    {
        pnlObjectSettings.clear();
        
        if (selectedObj != null)
        {
            String layer = selectedObj.layer.equals("common") ? "Common" : "Layer"+selectedObj.layer.substring(5).toUpperCase();
            lbSelected.setText(String.format("%1$s (%2$s, %3$s)", selectedObj.dbInfo.name, selectedObj.zone, layer));
            btnDeselect.setEnabled(true);
            
            LinkedList layerlist = new LinkedList();
            layerlist.add("Common");
            for (int l = 0; l < 26; l++)
            {
                String ls = String.format("Layer%1$c", 'A'+l);
                if (curZoneArc.objects.containsKey(ls.toLowerCase()))
                    layerlist.add(ls);
            }
            
            pnlObjectSettings.addCategory("obj_general", "General settings");
            pnlObjectSettings.addField("name", "Object", "objname", null, selectedObj.name);
            if (galaxyMode)
                pnlObjectSettings.addField("zone", "Zone", "list", galaxyArc.zoneList, selectedObj.zone);
            pnlObjectSettings.addField("layer", "Layer", "list", layerlist, layer);

            pnlObjectSettings.addCategory("obj_position", "Position");
            pnlObjectSettings.addField("pos_x", "X position", "float", null, selectedObj.position.x);
            pnlObjectSettings.addField("pos_y", "Y position", "float", null, selectedObj.position.y);
            pnlObjectSettings.addField("pos_z", "Z position", "float", null, selectedObj.position.z);
            pnlObjectSettings.addField("dir_x", "X rotation", "float", null, selectedObj.rotation.x);
            pnlObjectSettings.addField("dir_y", "Y rotation", "float", null, selectedObj.rotation.y);
            pnlObjectSettings.addField("dir_z", "Z rotation", "float", null, selectedObj.rotation.z);
            pnlObjectSettings.addField("scale_x", "X scale", "float", null, selectedObj.scale.x);
            pnlObjectSettings.addField("scale_y", "Y scale", "float", null, selectedObj.scale.y);
            pnlObjectSettings.addField("scale_z", "Z scale", "float", null, selectedObj.scale.z);
            
            // TODO nice object args (ObjectDB integration)
            
            pnlObjectSettings.addCategory("obj_args", "Object arguments");
            pnlObjectSettings.addField("Obj_arg0", "Obj_arg0", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg0")));
            pnlObjectSettings.addField("Obj_arg1", "Obj_arg1", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg1")));
            pnlObjectSettings.addField("Obj_arg2", "Obj_arg2", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg2")));
            pnlObjectSettings.addField("Obj_arg3", "Obj_arg3", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg3")));
            if (selectedObj.file.equalsIgnoreCase("objinfo"))
            {
                pnlObjectSettings.addField("Obj_arg4", "Obj_arg4", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg4")));
                pnlObjectSettings.addField("Obj_arg5", "Obj_arg5", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg5")));
                pnlObjectSettings.addField("Obj_arg6", "Obj_arg6", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg6")));
                pnlObjectSettings.addField("Obj_arg7", "Obj_arg7", "text", null, String.format("%1$08X",selectedObj.data.get("Obj_arg7")));
                
                pnlObjectSettings.addCategory("obj_objinfo", "Object settings");
                pnlObjectSettings.addField("MessageId", "Message ID", "int", null, selectedObj.data.get("MessageId"));
                if (curZoneArc.gameMask == 2)
                    pnlObjectSettings.addField("GeneratorID", "Generator ID", "int", null, selectedObj.data.get("GeneratorID"));
            }
            else if (selectedObj.file.equalsIgnoreCase("mappartsinfo"))
            {
                pnlObjectSettings.addCategory("obj_mappartsinfo", "Map part settings");
                pnlObjectSettings.addField("MoveConditionType", "Move condition", "int", null, selectedObj.data.get("MoveConditionType"));
                pnlObjectSettings.addField("RotateSpeed", "Rotate speed", "int", null, selectedObj.data.get("RotateSpeed"));
                pnlObjectSettings.addField("RotateAngle", "Rotate angle", "int", null, selectedObj.data.get("RotateAngle"));
                pnlObjectSettings.addField("RotateAxis", "Rotate axis", "int", null, selectedObj.data.get("RotateAxis"));
                pnlObjectSettings.addField("RotateAccelType", "Rotate accel type", "int", null, selectedObj.data.get("RotateAccelType"));
                pnlObjectSettings.addField("RotateStopTime", "Rotate stop time", "int", null, selectedObj.data.get("RotateStopTime"));
                pnlObjectSettings.addField("RotateType", "Rotate type", "int", null, selectedObj.data.get("RotateType"));
                pnlObjectSettings.addField("ShadowType", "Shadow type", "int", null, selectedObj.data.get("ShadowType"));
                pnlObjectSettings.addField("SignMotionType", "Rotate axis", "int", null, selectedObj.data.get("SignMotionType"));
                pnlObjectSettings.addField("[4137EDFD]", "[4137EDFD]", "int", null, selectedObj.data.get(0x4137EDFD));
                pnlObjectSettings.addField("FarClip", "Clip distance", "int", null, selectedObj.data.get("FarClip"));
                if (curZoneArc.gameMask == 2)
                    pnlObjectSettings.addField("ParentId", "Parent ID", "int", null, selectedObj.data.get("ParentId"));
            }
            
            pnlObjectSettings.addCategory("obj_misc", "Misc. settings");
            pnlObjectSettings.addField("l_id", "l_id", "int", null, selectedObj.data.get("l_id"));
            pnlObjectSettings.addField("CameraSetId", "CameraSetId", "int", null, selectedObj.data.get("CameraSetId"));
            pnlObjectSettings.addField("SW_APPEAR", "SW_APPEAR", "int", null, selectedObj.data.get("SW_APPEAR"));
            pnlObjectSettings.addField("SW_DEAD", "SW_DEAD", "int", null, selectedObj.data.get("SW_DEAD"));
            pnlObjectSettings.addField("SW_A", "SW_A", "int", null, selectedObj.data.get("SW_A"));
            pnlObjectSettings.addField("SW_B", "SW_B", "int", null, selectedObj.data.get("SW_B"));
            if (curZoneArc.gameMask == 2)
            {
                pnlObjectSettings.addField("SW_AWAKE", "SW_AWAKE", "int", null, selectedObj.data.get("SW_AWAKE"));
                pnlObjectSettings.addField("SW_PARAM", "SW_PARAM", "int", null, selectedObj.data.get("SW_PARAM"));
                pnlObjectSettings.addField("ParamScale", "ParamScale", "float", null, selectedObj.data.get("ParamScale"));
            }
            else
                pnlObjectSettings.addField("[4F11491C]", "[4F11491C]", "int", null, selectedObj.data.get(0x4F11491C));
            pnlObjectSettings.addField("CastId", "CastId", "int", null, selectedObj.data.get("CastId"));
            pnlObjectSettings.addField("ViewGroupId", "ViewGroupId", "int", null, selectedObj.data.get("ViewGroupId"));
            pnlObjectSettings.addField("ShapeModelNo", "ShapeModelNo", "int", null, selectedObj.data.get("ShapeModelNo"));
            pnlObjectSettings.addField("CommonPath_ID", "CommonPath_ID", "int", null, selectedObj.data.get("CommonPath_ID"));
            pnlObjectSettings.addField("ClippingGroupId", "ClippingGroupId", "int", null, selectedObj.data.get("ClippingGroupId"));
            pnlObjectSettings.addField("GroupId", "GroupId", "int", null, selectedObj.data.get("GroupId"));
            pnlObjectSettings.addField("DemoGroupId", "DemoGroupId", "int", null, selectedObj.data.get("DemoGroupId"));
            if (curZoneArc.gameMask == 2 || selectedObj.file.equalsIgnoreCase("objinfo"))
                pnlObjectSettings.addField("MapParts_ID", "MapParts_ID", "int", null, selectedObj.data.get("MapParts_ID"));
            if (curZoneArc.gameMask == 2)
                pnlObjectSettings.addField("Obj_ID", "Obj_ID", "int", null, selectedObj.data.get("Obj_ID"));

            pnlObjectSettings.addTermination();
        }
        else
        {
            lbSelected.setText("none");
            btnDeselect.setEnabled(false);
        }
        
        pnlObjectSettings.validate();
        pnlObjectSettings.repaint();
    }
    
    private void lbScenarioListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_lbScenarioListValueChanged
    {//GEN-HEADEREND:event_lbScenarioListValueChanged
        if (evt.getValueIsAdjusting())
        {
            return;
        }
        if (lbScenarioList.getSelectedValue() == null)
        {
            return;
        }
        
        curScenarioID = lbScenarioList.getSelectedIndex();
        curScenario = galaxyArc.scenarioData.get(curScenarioID);

        DefaultListModel zonelist = new DefaultListModel();
        lbZoneList.setModel(zonelist);
        for (String zone : galaxyArc.zoneList)
        {
            String layerstr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ------";
            int layermask = (int) curScenario.get(zone);
            String layers = "Common+";
            for (int i = 0; i < 32; i++)
            {
                if ((layermask & (1 << i)) != 0)
                {
                    layers += layerstr.charAt(i);
                }
            }
            if (layers.equals("Common+"))
            {
                layers = "Common";
            }

            zonelist.addElement(zone + " [" + layers + "]");
        }

        lbZoneList.setSelectedIndex(0);
    }//GEN-LAST:event_lbScenarioListValueChanged

    private void setStatusText()
    {
        if (galaxyMode)
            lbStatusLabel.setText("Editing scenario " + lbScenarioList.getSelectedValue() + ", zone " + curZone);
        else
            lbStatusLabel.setText("Editing zone " + curZone);
    }
    
    private void lbZoneListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_lbZoneListValueChanged
    {//GEN-HEADEREND:event_lbZoneListValueChanged
        if (evt.getValueIsAdjusting())
        {
            return;
        }
        if (lbZoneList.getSelectedValue() == null)
        {
            return;
        }
        
        btnEditZone.setEnabled(true);
        
        int selid = lbZoneList.getSelectedIndex();
        curZone = galaxyArc.zoneList.get(selid);
        curZoneArc = zoneArcs.get(curZone);
        
        int layermask = (int)curScenario.get(curZone);
        populateObjectList(layermask << 1 | 1);

        setStatusText();
        
        glCanvas.repaint();
    }//GEN-LAST:event_lbZoneListValueChanged

    private void populateObjectList(int layermask)
    {
        DefaultTreeModel objlist = (DefaultTreeModel)tvObjectList.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(curZone);
        objlist.setRoot(root);
        
        ObjListTreeNode objnode = new ObjListTreeNode();
        objnode.setUserObject("Objects");
        root.add(objnode);
        
        for (java.util.List<LevelObject> objs : curZoneArc.objects.values())
        {
            for (LevelObject obj : objs)
            {
                if (!obj.layer.equals("common"))
                {
                    int layernum = obj.layer.charAt(5) - 'a';
                    if ((layermask & (2 << layernum)) == 0) continue;
                }
                else if ((layermask & 1) == 0) continue;
                
                objnode.addObject(obj);
            }
        }
    }
    
    private void layerSelectChange(int index, boolean status)
    {
        JCheckBox cbx = (JCheckBox)lbLayersList.getModel().getElementAt(index);
        int layer = cbx.getText().equals("Common") ? 1 : (2 << (cbx.getText().charAt(5) - 'A'));
        
        if (status)
            zoneModeLayerBitmask |= layer;
        else
            zoneModeLayerBitmask &= ~layer;
        
        rerenderTasks.add("allobjects:");
        glCanvas.repaint();
    }
    
    private void btnDeselectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDeselectActionPerformed
    {//GEN-HEADEREND:event_btnDeselectActionPerformed
        rerenderTasks.add("zone:"+selectedObj.zone);
        selectedVal = 0xFFFFFF;
        selectedObj = null;
        selectionChanged();
        glCanvas.repaint();
    }//GEN-LAST:event_btnDeselectActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSaveActionPerformed
    {//GEN-HEADEREND:event_btnSaveActionPerformed
        try
        {
            for (ZoneArchive zonearc : zoneArcs.values())
                zonearc.save();
            
            lbStatusLabel.setText("Changes saved.");
            
            if (!galaxyMode && parentForm != null)
                parentForm.updateZone(galaxyName);
            else
            {
                for (GalaxyEditorForm form : childZoneEditors.values())
                    form.updateZone(form.galaxyName);
            }
        }
        catch (IOException ex)
        {
            lbStatusLabel.setText("Failed to save changes: "+ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnEditZoneActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnEditZoneActionPerformed
    {//GEN-HEADEREND:event_btnEditZoneActionPerformed
        if (childZoneEditors.containsKey(curZone))
        {
            if (!childZoneEditors.get(curZone).isVisible())
                childZoneEditors.remove(curZone);
            else
            {
                childZoneEditors.get(curZone).toFront();
                return;
            }
        }
        
        GalaxyEditorForm form = new GalaxyEditorForm(this, curZoneArc);
        form.setVisible(true);
        childZoneEditors.put(curZone, form);
    }//GEN-LAST:event_btnEditZoneActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        // TODO save confirm!
        
        if (galaxyMode)
        {
            for (GalaxyEditorForm form : childZoneEditors.values())
                form.dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void tvObjectListValueChanged(javax.swing.event.TreeSelectionEvent evt)//GEN-FIRST:event_tvObjectListValueChanged
    {//GEN-HEADEREND:event_tvObjectListValueChanged
        String lastzone = "lolz";
        if (selectedObj != null)
        {
            rerenderTasks.add("zone:"+selectedObj.zone);
            lastzone = selectedObj.zone;
        }
        
        if (evt.getNewLeadSelectionPath() == null)
        {
            selectedVal = 0xFFFFFF;
            selectedObj = null;
        }
        else
        {
            TreeNode selnode = (TreeNode)evt.getNewLeadSelectionPath().getLastPathComponent();
            if (selnode.getClass() != ObjTreeNode.class)
            {
                selectedVal = 0xFFFFFF;
                selectedObj = null;
            }
            else
            {
                selectedObj = ((ObjTreeNode)selnode).object;
                selectedVal = selectedObj.uniqueID;
                if (!lastzone.equals(selectedObj.zone))
                    rerenderTasks.add("zone:"+selectedObj.zone);
            }
        }
        
        selectionChanged();
        glCanvas.repaint();
    }//GEN-LAST:event_tvObjectListValueChanged

    
    public void applySubzoneRotation(Vector3 delta)
    {
        if (!galaxyMode) return;

        String szkey = String.format("%1$d/%2$s", curScenarioID, curZone);
        if (subZoneData.containsKey(szkey))
        {
            SubZoneData szdata = subZoneData.get(szkey);

            float xcos = (float)Math.cos(-(szdata.rotation.x * Math.PI) / 180f);
            float xsin = (float)Math.sin(-(szdata.rotation.x * Math.PI) / 180f);
            float ycos = (float)Math.cos(-(szdata.rotation.y * Math.PI) / 180f);
            float ysin = (float)Math.sin(-(szdata.rotation.y * Math.PI) / 180f);
            float zcos = (float)Math.cos(-(szdata.rotation.z * Math.PI) / 180f);
            float zsin = (float)Math.sin(-(szdata.rotation.z * Math.PI) / 180f);

            float x1 = (delta.x * zcos) - (delta.y * zsin);
            float y1 = (delta.x * zsin) + (delta.y * zcos);
            float x2 = (x1 * ycos) + (delta.z * ysin);
            float z2 = -(x1 * ysin) + (delta.z * ycos);
            float y3 = (y1 * xcos) - (z2 * xsin);
            float z3 = (y1 * xsin) + (z2 * xcos);

            delta.x = x2;
            delta.y = y3;
            delta.z = z3;
        }
    }

    private Vector3 get3DCoords(Point pt, float depth)
    {
        Vector3 ret = new Vector3(
                camPosition.x * scaledown,
                camPosition.y * scaledown,
                camPosition.z * scaledown);
        depth *= scaledown;

        ret.x -= (depth * (float)Math.cos(camRotation.x) * (float)Math.cos(camRotation.y));
        ret.y -= (depth * (float)Math.sin(camRotation.y));
        ret.z -= (depth * (float)Math.sin(camRotation.x) * (float)Math.cos(camRotation.y));

        float x = (pt.x - (glCanvas.getWidth() / 2f)) * pixelFactorX * depth;
        float y = -(pt.y - (glCanvas.getHeight() / 2f)) * pixelFactorY * depth;

        ret.x += (x * (float)Math.sin(camRotation.x)) - (y * (float)Math.sin(camRotation.y) * (float)Math.cos(camRotation.x));
        ret.y += y * (float)Math.cos(camRotation.y);
        ret.z += -(x * (float)Math.cos(camRotation.x)) - (y * (float)Math.sin(camRotation.y) * (float)Math.sin(camRotation.x));

        return ret;
    }
    
    private void addObject(Point where)
    {
        Vector3 pos = get3DCoords(where, Math.min(pickingDepth, 1f));
        
        if (galaxyMode)
        {
            String szkey = String.format("%1$d/%2$s", curScenarioID, curZone);
            if (subZoneData.containsKey(szkey))
            {
                SubZoneData szdata = subZoneData.get(szkey);
                Vector3.subtract(pos, szdata.position, pos);
                applySubzoneRotation(pos);
            }
        }
        
        String filepath;
        if (ObjectDB.objects.containsKey(objectBeingAdded))
            filepath = ObjectDB.objects.get(objectBeingAdded).preferredFile.replace("<layer>", addingOnLayer);
        else
            filepath = "Placement/" + addingOnLayer + "/ObjInfo";
        
        LevelObject newobj = new LevelObject(curZone, filepath, curZoneArc.gameMask, objectBeingAdded, pos);
        
        int uid = 0;
        while (globalObjList.containsKey(uid)) uid++;
        if (uid > maxUniqueID) maxUniqueID = uid;
        newobj.uniqueID = uid;
        globalObjList.put(uid, newobj);
        
        curZoneArc.objects.get(addingOnLayer.toLowerCase()).add(newobj);
        
        DefaultTreeModel objlist = (DefaultTreeModel)tvObjectList.getModel();
        ObjListTreeNode listnode = (ObjListTreeNode)((DefaultMutableTreeNode)objlist.getRoot()).getChildAt(0);
        TreeNode newnode = listnode.addObject(newobj);
        objlist.nodesWereInserted(listnode, new int[] { listnode.getIndex(newnode) });
        
        rerenderTasks.add("addobj:"+new Integer(uid).toString());
        rerenderTasks.add("zone:"+curZone);
        glCanvas.repaint();
    }
    
    private void deleteObject(int uid)
    {
        LevelObject obj = globalObjList.get(uid);
        zoneArcs.get(obj.zone).objects.get(obj.layer).remove(obj);
        rerenderTasks.add("delobj:"+new Integer(uid).toString());
        rerenderTasks.add("zone:"+obj.zone);
        
        DefaultTreeModel objlist = (DefaultTreeModel)tvObjectList.getModel();
        ObjListTreeNode listnode = (ObjListTreeNode)((DefaultMutableTreeNode)objlist.getRoot()).getChildAt(0);
        MutableTreeNode thenode = (MutableTreeNode)listnode.children.get(selectedObj.uniqueID);
        int theid = listnode.getIndex(thenode);
        objlist.removeNodeFromParent(thenode);
        objlist.nodesWereRemoved(listnode, new int[] { theid }, new Object[] { thenode });
        
        glCanvas.repaint();
    }
    
    
    private void tgbDeleteObjectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgbDeleteObjectActionPerformed
    {//GEN-HEADEREND:event_tgbDeleteObjectActionPerformed
        if (selectedObj != null)
        {
            if (tgbDeleteObject.isSelected())
            {
                deleteObject(selectedObj.uniqueID);
                selectedVal = 0xFFFFFF;
                selectedObj = null;
                selectionChanged();
            }
            tgbDeleteObject.setSelected(false);
        }
        else
        {
            if (!tgbDeleteObject.isSelected())
            {
                deletingObjects = false;
                setStatusText();
            }
            else
            {
                deletingObjects = true;
                lbStatusLabel.setText("Click the object you want to delete. Hold Shift to delete multiple objects. Right-click to abort.");
            }
        }
    }//GEN-LAST:event_tgbDeleteObjectActionPerformed

    private void tgbAddObjectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tgbAddObjectActionPerformed
    {//GEN-HEADEREND:event_tgbAddObjectActionPerformed
        if (!tgbAddObject.isSelected())
        {
            objectBeingAdded = "";
            setStatusText();
            return;
        }
        
        ObjectSelectForm form = new ObjectSelectForm(this, curZoneArc.gameMask, null);
        form.setVisible(true);
        if (form.selectedObject.isEmpty())
        {
            tgbAddObject.setSelected(false);
            return;
        }
        
        lbStatusLabel.setText("Click the level view to place your object. Hold Shift to place multiple objects. Right-click to abort.");
        objectBeingAdded = form.selectedObject;
        addingOnLayer = form.selectedLayer;
    }//GEN-LAST:event_tgbAddObjectActionPerformed

    public void propPanelPropertyChanged(String propname, Object value)
    {
        if (propname.equals("name"))
        {
            selectedObj.name = (String)value;
            selectedObj.loadDBInfo();
            
            DefaultTreeModel objlist = (DefaultTreeModel)tvObjectList.getModel();
            ObjListTreeNode listnode = (ObjListTreeNode)((DefaultMutableTreeNode)objlist.getRoot()).getChildAt(0);
            objlist.nodeChanged(listnode.children.get(selectedObj.uniqueID));
            
            rerenderTasks.add("object:"+new Integer(selectedObj.uniqueID).toString());
            rerenderTasks.add("zone:"+selectedObj.zone);
            glCanvas.repaint();
        }
        else if (propname.equals("zone"))
        {
            String oldzone = selectedObj.zone;
            String newzone = (String)value;
            
            selectedObj.zone = newzone;
            zoneArcs.get(oldzone).objects.get(selectedObj.layer).remove(selectedObj);
            if (zoneArcs.get(newzone).objects.containsKey(selectedObj.layer))
                zoneArcs.get(newzone).objects.get(selectedObj.layer).add(selectedObj);
            else
            {
                selectedObj.layer = "common";
                zoneArcs.get(newzone).objects.get(selectedObj.layer).add(selectedObj);
            }
            
            DefaultTreeModel objlist = (DefaultTreeModel)tvObjectList.getModel();
            ObjListTreeNode listnode = (ObjListTreeNode)((DefaultMutableTreeNode)objlist.getRoot()).getChildAt(0);
            objlist.nodeChanged(listnode.children.get(selectedObj.uniqueID));
            
            selectionChanged();
            rerenderTasks.add("zone:"+oldzone);
            rerenderTasks.add("zone:"+newzone);
            glCanvas.repaint();
        }
        else if (propname.equals("layer"))
        {
            String oldlayer = selectedObj.layer;
            String newlayer = ((String)value).toLowerCase();
            
            selectedObj.layer = newlayer;
            curZoneArc.objects.get(oldlayer).remove(selectedObj);
            curZoneArc.objects.get(newlayer).add(selectedObj);
            
            rerenderTasks.add("zone:"+selectedObj.zone);
            glCanvas.repaint();
        }
        else if (propname.startsWith("pos_") || propname.startsWith("dir_") || propname.startsWith("scale_"))
        {
            switch (propname)
            {
                case "pos_x": selectedObj.position.x = (float)(double)value; break;
                case "pos_y": selectedObj.position.y = (float)(double)value; break;
                case "pos_z": selectedObj.position.z = (float)(double)value; break;
                case "dir_x": selectedObj.rotation.x = (float)(double)value; break;
                case "dir_y": selectedObj.rotation.y = (float)(double)value; break;
                case "dir_z": selectedObj.rotation.z = (float)(double)value; break;
                case "scale_x": selectedObj.scale.x = (float)(double)value; break;
                case "scale_y": selectedObj.scale.y = (float)(double)value; break;
                case "scale_z": selectedObj.scale.z = (float)(double)value; break;
            }
            
            if (propname.startsWith("scale_") && selectedObj.renderer.hasSpecialScaling())
                rerenderTasks.add("object:"+new Integer(selectedObj.uniqueID).toString());
            
            rerenderTasks.add("zone:"+selectedObj.zone);
            glCanvas.repaint();
        }
        else if (propname.startsWith("Obj_arg"))
        {
            selectedObj.data.put(propname, (int)Long.parseLong((String)value, 16));
        }
        else if (propname.startsWith("["))
        {
            selectedObj.data.put((int)Long.parseLong(propname.substring(1, 9)), (int)value);
        }
        else
        {
            selectedObj.data.put(propname, (int)value);
        }
    }

    
    public class GalaxyRenderer implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener
    {
        public GalaxyRenderer()
        {
            super();
        }
        
        @Override
        public void init(GLAutoDrawable glad)
        {
            GL2 gl = glad.getGL().getGL2();
            
            RendererCache.setRefContext(glad.getContext());
            
            lastMouseMove = new Point(-1, -1);
            pickingFrameBuffer = IntBuffer.allocate(9);
            pickingDepthBuffer = FloatBuffer.allocate(1);
            pickingDepth = 1f;
            
            isDragging = false;
            pickingCapture = false;
            underCursor = 0xFFFFFF;
            selectedVal = 0xFFFFFF;
            selectedObj = null;
            objectBeingAdded = "";
            addingOnLayer = "";
            deletingObjects = false;
            
            renderinfo = new GLRenderer.RenderInfo();
            renderinfo.drawable = glad;
            renderinfo.renderMode = GLRenderer.RenderMode.OPAQUE;
            
            camDistance = 1f;
            camRotation = new Vector2(0f, 0f);
            camTarget = new Vector3(0f, 0f, 0f);
            camPosition = new Vector3(0f, 0f, 0f);
            updateCamera();
            
            if (parentForm == null)
            {
                for (LevelObject obj : globalObjList.values())
                    obj.initRenderer(renderinfo);
            }
            
            objDisplayLists = new HashMap<>();
            zoneDisplayLists = new HashMap<>();
            renderinfo.renderMode = GLRenderer.RenderMode.PICKING; renderAllObjects(gl);
            renderinfo.renderMode = GLRenderer.RenderMode.OPAQUE; renderAllObjects(gl);
            renderinfo.renderMode = GLRenderer.RenderMode.TRANSLUCENT; renderAllObjects(gl);
            
            rerenderTasks = new PriorityQueue<>();
            
            gl.glFrontFace(GL2.GL_CW);
            
            inited = true;
        }
        
        
        private void renderSelectHighlight(GL2 gl)
        {
            gl.glUseProgram(0);
            for (int i = 0; i < 8; i++)
            {
                gl.glActiveTexture(GL2.GL_TEXTURE0 + i);
                gl.glDisable(GL2.GL_TEXTURE_2D);
            }
            
            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendEquation(GL2.GL_FUNC_ADD);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL2.GL_COLOR_LOGIC_OP);
            gl.glDisable(GL2.GL_ALPHA_TEST);
            
            gl.glDepthMask(false);

            gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(-1f, -1f);
            
            renderinfo.drawable = glCanvas;
            GLRenderer.RenderMode oldmode = renderinfo.renderMode;
            renderinfo.renderMode = GLRenderer.RenderMode.PICKING;
            gl.glColor4f(1f, 1f, 0.75f, 0.3f);
            selectedObj.render(renderinfo);
            
            gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
            renderinfo.renderMode = oldmode;
        }
        
        private void renderAllObjects(GL2 gl)
        {
            int mode = -1;
            switch (renderinfo.renderMode)
            {
                case PICKING: mode = 0; break;
                case OPAQUE: mode = 1; break;
                case TRANSLUCENT: mode = 2; break;
            }
            
            if (galaxyMode)
            {
                for (String zone : galaxyArc.zoneList)
                    prerenderZone(gl, zone);
                
                for (int s = 0; s < galaxyArc.scenarioData.size(); s++)
                {
                    if (!zoneDisplayLists.containsKey(s))
                        zoneDisplayLists.put(s, new int[] {0,0,0});

                    int dl = zoneDisplayLists.get(s)[mode];
                    if (dl == 0)
                    {
                        dl = gl.glGenLists(1);
                        zoneDisplayLists.get(s)[mode] = dl;
                    }
                    gl.glNewList(dl, GL2.GL_COMPILE);

                    Bcsv.Entry scenario = galaxyArc.scenarioData.get(s);
                    renderZone(gl, scenario, galaxyName, (int)scenario.get(galaxyName), 0);

                    gl.glEndList();
                }
            }
            else
            {
                prerenderZone(gl, galaxyName);
                
                if (!zoneDisplayLists.containsKey(0))
                    zoneDisplayLists.put(0, new int[] {0,0,0});

                int dl = zoneDisplayLists.get(0)[mode];
                if (dl == 0)
                {
                    dl = gl.glGenLists(1);
                    zoneDisplayLists.get(0)[mode] = dl;
                }
                gl.glNewList(dl, GL2.GL_COMPILE);

                renderZone(gl, null, galaxyName, zoneModeLayerBitmask, 99);

                gl.glEndList();
            }
        }
        
        private void prerenderZone(GL2 gl, String zone)
        {
            int mode = -1;
            switch (renderinfo.renderMode)
            {
                case PICKING: mode = 0; break;
                case OPAQUE: mode = 1; break;
                case TRANSLUCENT: mode = 2; break;
            }
            
            ZoneArchive zonearc = zoneArcs.get(zone);
            Set<String> layers = zonearc.objects.keySet();
            for (String layer : layers)
            {
                String key = zone + "/" + layer.toLowerCase();
                if (!objDisplayLists.containsKey(key))
                    objDisplayLists.put(key, new int[] {0,0,0});
                
                int dl = objDisplayLists.get(key)[mode];
                if (dl == 0) 
                { 
                    dl = gl.glGenLists(1); 
                    objDisplayLists.get(key)[mode] = dl;
                }
                
                gl.glNewList(dl, GL2.GL_COMPILE);
                
                for (LevelObject obj : zonearc.objects.get(layer))
                {
                    if (mode == 0) 
                    {
                        // set color to the object's uniqueID (RGB)
                        gl.glColor4ub(
                                (byte)(obj.uniqueID >>> 16), 
                                (byte)(obj.uniqueID >>> 8), 
                                (byte)obj.uniqueID, 
                                (byte)0xFF);
                    }
                    obj.render(renderinfo);
                }
                
                if (mode == 2 && selectedObj != null && selectedObj.zone.equals(zone))
                {
                    renderSelectHighlight(gl);
                }

                gl.glEndList();
            }
        }
        
        private void renderZone(GL2 gl, Bcsv.Entry scenario, String zone, int layermask, int level)
        {
            String alphabet = "abcdefghijklmnopqrstuvwxyz------";
            int mode = -1;
            switch (renderinfo.renderMode)
            {
                case PICKING: mode = 0; break;
                case OPAQUE: mode = 1; break;
                case TRANSLUCENT: mode = 2; break;
            }
            
            if (galaxyMode)
                gl.glCallList(objDisplayLists.get(zone + "/common")[mode]);
            else
            {
                if ((layermask & 1) != 0) gl.glCallList(objDisplayLists.get(zone + "/common")[mode]);
                layermask >>= 1;
            }
            
            for (int l = 0; l < 32; l++)
            {
                if ((layermask & (1 << l)) != 0)
                    gl.glCallList(objDisplayLists.get(zone + "/layer" + alphabet.charAt(l))[mode]);
            }
            
            if (level < 5)
            {
                for (Bcsv.Entry subzone : zoneArcs.get(zone).subZones.get("common"))
                {
                    gl.glPushMatrix();
                    gl.glTranslatef((float)subzone.get("pos_x"), (float)subzone.get("pos_y"), (float)subzone.get("pos_z"));
                    gl.glRotatef((float)subzone.get("dir_z"), 0f, 0f, 1f);
                    gl.glRotatef((float)subzone.get("dir_y"), 0f, 1f, 0f);
                    gl.glRotatef((float)subzone.get("dir_x"), 1f, 0f, 0f);

                    String zonename = (String)subzone.get("name");
                    renderZone(gl, scenario, zonename, (int)scenario.get(zonename), level + 1);

                    gl.glPopMatrix();
                }
                
                for (int l = 0; l < 32; l++)
                {
                    if ((layermask & (1 << l)) != 0)
                    {
                        for (Bcsv.Entry subzone : zoneArcs.get(zone).subZones.get("layer" + alphabet.charAt(l)))
                        {
                            gl.glPushMatrix();
                            gl.glTranslatef((float)subzone.get("pos_x"), (float)subzone.get("pos_y"), (float)subzone.get("pos_z"));
                            gl.glRotatef((float)subzone.get("dir_z"), 0f, 0f, 1f);
                            gl.glRotatef((float)subzone.get("dir_y"), 0f, 1f, 0f);
                            gl.glRotatef((float)subzone.get("dir_x"), 1f, 0f, 0f);

                            String zonename = (String)subzone.get("name");
                            renderZone(gl, scenario, zonename, (int)scenario.get(zonename), level + 1);

                            gl.glPopMatrix();
                        }
                    }
                }
            }
        }
        

        @Override
        public void dispose(GLAutoDrawable glad)
        {
            GL2 gl = glad.getGL().getGL2();
            renderinfo.drawable = glad;
            
            for (int[] dls : zoneDisplayLists.values())
            {
                gl.glDeleteLists(dls[0], 1);
                gl.glDeleteLists(dls[1], 1);
                gl.glDeleteLists(dls[2], 1);
            }
            
            for (int[] dls : objDisplayLists.values())
            {
                gl.glDeleteLists(dls[0], 1);
                gl.glDeleteLists(dls[1], 1);
                gl.glDeleteLists(dls[2], 1);
            }
            
            if (parentForm == null)
            {
                for (LevelObject obj : globalObjList.values())
                    obj.closeRenderer(renderinfo);
            }
            
            RendererCache.clearRefContext();
        }
        
        private void doRerenderTasks()
        {
            GL2 gl = renderinfo.drawable.getGL().getGL2();
            
            while (!rerenderTasks.isEmpty())
            {
                String[] task = rerenderTasks.poll().split(":");
                switch (task[0])
                {
                    case "zone":
                        renderinfo.renderMode = GLRenderer.RenderMode.PICKING;      prerenderZone(gl, task[1]);
                        renderinfo.renderMode = GLRenderer.RenderMode.OPAQUE;       prerenderZone(gl, task[1]);
                        renderinfo.renderMode = GLRenderer.RenderMode.TRANSLUCENT;  prerenderZone(gl, task[1]);
                        break;
                        
                    case "object":
                        {
                            int objid = Integer.parseInt(task[1]);
                            LevelObject obj = globalObjList.get(objid);
                            obj.closeRenderer(renderinfo);
                            obj.initRenderer(renderinfo);
                        }
                        break;
                        
                    case "addobj":
                        {
                            int objid = Integer.parseInt(task[1]);
                            LevelObject obj = globalObjList.get(objid);
                            obj.initRenderer(renderinfo);
                        }
                        break;
                        
                    case "delobj":
                        {
                            int objid = Integer.parseInt(task[1]);
                            LevelObject obj = globalObjList.get(objid);
                            obj.closeRenderer(renderinfo);
                            globalObjList.remove(obj.uniqueID);
                        }
                        break;
                        
                    case "allobjects":
                        renderinfo.renderMode = GLRenderer.RenderMode.PICKING;      renderAllObjects(gl);
                        renderinfo.renderMode = GLRenderer.RenderMode.OPAQUE;       renderAllObjects(gl);
                        renderinfo.renderMode = GLRenderer.RenderMode.TRANSLUCENT;  renderAllObjects(gl);
                        break;
                }
            }
        }
        
        @Override
        public void display(GLAutoDrawable glad)
        {
            if (!inited) return;
            GL2 gl = glad.getGL().getGL2();
            renderinfo.drawable = glad;
            
            doRerenderTasks();
            
            // Rendering pass 1 -- fakecolor rendering
            // the results are used to determine which object is pointed at
            
            gl.glClearColor(1f, 1f, 1f, 1f);
            gl.glClearDepth(1f);
            gl.glClearStencil(0);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);
            
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadMatrixf(modelViewMatrix.m, 0);
            
            gl.glUseProgram(0);
            gl.glDisable(GL2.GL_ALPHA_TEST);
            gl.glDisable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_COLOR_LOGIC_OP);
            gl.glDisable(GL2.GL_LIGHTING);
            gl.glDisable(GL2.GL_DITHER);
            gl.glDisable(GL2.GL_LINE_SMOOTH);
            gl.glDisable(GL2.GL_POLYGON_SMOOTH);
            
            for (int i = 0; i < 8; i++)
            {
                gl.glActiveTexture(GL2.GL_TEXTURE0 + i);
                gl.glDisable(GL2.GL_TEXTURE_2D);
            }
            
            gl.glCallList(zoneDisplayLists.get(curScenarioID)[0]);
            
            gl.glDepthMask(true);
            
            gl.glFlush();
            
            gl.glReadPixels(lastMouseMove.x - 1, glad.getHeight() - lastMouseMove.y + 1, 3, 3, GL2.GL_BGRA, GL2.GL_UNSIGNED_INT_8_8_8_8_REV, pickingFrameBuffer);
            gl.glReadPixels(lastMouseMove.x, glad.getHeight() - lastMouseMove.y, 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, pickingDepthBuffer);
            pickingDepth = -(zFar * zNear / (pickingDepthBuffer.get(0) * (zFar - zNear) - zFar));
           
            // Rendering pass 2 -- standard rendering
            // (what the user will see)

            gl.glClearColor(0f, 0f, 0.125f, 1f);
            gl.glClearDepth(1f);
            gl.glClearStencil(0);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);
            
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadMatrixf(modelViewMatrix.m, 0);
            
            gl.glEnable(GL2.GL_TEXTURE_2D);
            
            if (Settings.fastDrag)
            {
                if (isDragging) 
                {
                    gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
                    gl.glPolygonMode(GL2.GL_BACK, GL2.GL_POINT);
                }
                else 
                    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            }
            
            gl.glCallList(zoneDisplayLists.get(curScenarioID)[1]);
            
            gl.glCallList(zoneDisplayLists.get(curScenarioID)[2]);
            
            gl.glDepthMask(true);
            gl.glUseProgram(0);
            for (int i = 0; i < 8; i++)
            {
                gl.glActiveTexture(GL2.GL_TEXTURE0 + i);
                gl.glDisable(GL2.GL_TEXTURE_2D);
            }
            gl.glDisable(GL2.GL_BLEND);
            gl.glDisable(GL2.GL_ALPHA_TEST);
            
            gl.glBegin(GL2.GL_LINES);
            gl.glColor4f(1f, 0f, 0f, 1f);
            gl.glVertex3f(0f, 0f, 0f);
            gl.glVertex3f(100000f, 0f, 0f);
            gl.glColor4f(0f, 1f, 0f, 1f);
            gl.glVertex3f(0f, 0f, 0f);
            gl.glVertex3f(0, 100000f, 0f);
            gl.glColor4f(0f, 0f, 1f, 1f);
            gl.glVertex3f(0f, 0f, 0f);
            gl.glVertex3f(0f, 0f, 100000f);
            gl.glEnd();
            
            glad.swapBuffers();
        }
        @Override
        public void reshape(GLAutoDrawable glad, int x, int y, int width, int height)
        {
            if (!inited) return;
            GL2 gl = glad.getGL().getGL2();
            
            gl.glViewport(x, y, width, height);
            
            float aspectRatio = (float)width / (float)height;
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            float ymax = zNear * (float)Math.tan(0.5f * fov);
            gl.glFrustum(
                    -ymax * aspectRatio, ymax * aspectRatio,
                    -ymax, ymax,
                    zNear, zFar);
            
            pixelFactorX = (2f * (float)Math.tan(fov * 0.5f) * aspectRatio) / (float)width;
            pixelFactorY = (2f * (float)Math.tan(fov * 0.5f)) / (float)height;
        }
        
        
        public void updateCamera()
        {
            Vector3 up;
            
            if (Math.cos(camRotation.y) < 0f)
            {
                upsideDown = true;
                up = new Vector3(0f, -1f, 0f);
            }
            else
            {
                upsideDown = false;
                up = new Vector3(0f, 1f, 0f);
            }
            
            camPosition.x = camDistance * (float)Math.cos(camRotation.x) * (float)Math.cos(camRotation.y);
            camPosition.y = camDistance * (float)Math.sin(camRotation.y);
            camPosition.z = camDistance * (float)Math.sin(camRotation.x) * (float)Math.cos(camRotation.y);
            
            Vector3.add(camPosition, camTarget, camPosition);
            
            modelViewMatrix = Matrix4.lookAt(camPosition, camTarget, up);
            Matrix4.mult(Matrix4.scale(1f / scaledown), modelViewMatrix, modelViewMatrix);
        }
        

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (!inited) return;
            
            float xdelta = e.getX() - lastMouseMove.x;
            float ydelta = e.getY() - lastMouseMove.y;
            
            if (!isDragging && (Math.abs(xdelta) >= 3f || Math.abs(ydelta) >= 3f))
            {
                pickingCapture = true;
                isDragging = true;
            }
            
            if (!isDragging)
                return;
            
            if (pickingCapture)
            {
                underCursor = pickingFrameBuffer.get(4) & 0xFFFFFF;
                depthUnderCursor = pickingDepth;
                pickingCapture = false;
            }
            
            lastMouseMove = e.getPoint();
            
            if (selectedObj != null && selectedVal == underCursor)
            {
                if (mouseButton == MouseEvent.BUTTON1)
                {
                    float objz = depthUnderCursor;
                    
                    xdelta *= pixelFactorX * objz * scaledown;
                    ydelta *= -pixelFactorY * objz * scaledown;
                    
                    Vector3 delta = new Vector3(
                            (xdelta * (float)Math.sin(camRotation.x)) - (ydelta * (float)Math.sin(camRotation.y) * (float)Math.cos(camRotation.x)),
                            ydelta * (float)Math.cos(camRotation.y),
                            -(xdelta * (float)Math.cos(camRotation.x)) - (ydelta * (float)Math.sin(camRotation.y) * (float)Math.sin(camRotation.x)));
                    applySubzoneRotation(delta);
                    
                    selectedObj.position.x += delta.x;
                    selectedObj.position.y += delta.y;
                    selectedObj.position.z += delta.z;
                    
                    pnlObjectSettings.setFieldValue("pos_x", selectedObj.position.x);
                    pnlObjectSettings.setFieldValue("pos_y", selectedObj.position.y);
                    pnlObjectSettings.setFieldValue("pos_z", selectedObj.position.z);
                    rerenderTasks.add("zone:"+selectedObj.zone);
                }
            }
            else
            {
                if (mouseButton == MouseEvent.BUTTON3)
                {
                    if (upsideDown) xdelta = -xdelta;
                    
                    xdelta *= 0.002f;
                    ydelta *= 0.002f;
                    
                    /*if (underCursor != 0xFFFFFF)
                    {
                        float dist = camDistance - depthUnderCursor;
                        if (dist > 0f)
                        {
                            camTarget.x -= 
                        }
                    }*/

                    camRotation.x -= xdelta;
                    camRotation.y -= ydelta;
                }
                else if (mouseButton == MouseEvent.BUTTON1)
                {
                    if (underCursor == 0xFFFFFF)
                    {
                        xdelta *= 0.005f;
                        ydelta *= 0.005f;
                    }
                    else
                    {
                        xdelta *= Math.min(0.005f, pixelFactorX * depthUnderCursor);
                        ydelta *= Math.min(0.005f, pixelFactorY * depthUnderCursor);
                    }

                    camTarget.x -= xdelta * (float)Math.sin(camRotation.x);
                    camTarget.x -= ydelta * (float)Math.cos(camRotation.x) * (float)Math.sin(camRotation.y);
                    camTarget.y += ydelta * (float)Math.cos(camRotation.y);
                    camTarget.z += xdelta * (float)Math.cos(camRotation.x);
                    camTarget.z -= ydelta * (float)Math.sin(camRotation.x) * (float)Math.sin(camRotation.y);
                }

                updateCamera();
            }
            
            e.getComponent().repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            if (!inited) return;
            
            lastMouseMove = e.getPoint();
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (!inited) return;
            if (mouseButton != MouseEvent.NOBUTTON) return;
            
            mouseButton = e.getButton();
            lastMouseMove = e.getPoint();
            
            isDragging = false;
            
            e.getComponent().repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (!inited) return;
            if (e.getButton() != mouseButton) return;
            
            mouseButton = MouseEvent.NOBUTTON;
            lastMouseMove = e.getPoint();
            boolean shiftpressed = (e.getModifiers() & 1) != 0;
            
            if (isDragging)
            {
                isDragging = false;
                if (Settings.fastDrag) e.getComponent().repaint();
                return;
            }
            
            int objid = pickingFrameBuffer.get(4);
            if (    objid != pickingFrameBuffer.get(1) ||
                    objid != pickingFrameBuffer.get(3) ||
                    objid != pickingFrameBuffer.get(5) ||
                    objid != pickingFrameBuffer.get(7))
                return;
            
            objid &= 0xFFFFFF;
            if (objid != 0xFFFFFF && !globalObjList.containsKey(objid))
                return;
            
            // no need to handle rerendering here: changing the treeview's selection
            // will trigger it
            
            if (e.getButton() == MouseEvent.BUTTON3)
            {
                if (!objectBeingAdded.isEmpty())
                {
                    objectBeingAdded = "";
                    tgbAddObject.setSelected(false);
                    setStatusText();
                }
                else if (deletingObjects)
                {
                    deletingObjects = false;
                    tgbDeleteObject.setSelected(false);
                    setStatusText();
                }
            }
            else
            {
                if (objid == selectedVal || objid == 0xFFFFFF)
                {
                    tvObjectList.setSelectionPath(null);
                }
                else if (!objectBeingAdded.isEmpty())
                {
                    addObject(lastMouseMove);
                    if (!shiftpressed)
                    {
                        objectBeingAdded = "";
                        tgbAddObject.setSelected(false);
                        setStatusText();
                    }
                }
                else if (deletingObjects)
                {
                    deleteObject(objid);
                    if (!shiftpressed) 
                    {
                        deletingObjects = false;
                        tgbDeleteObject.setSelected(false);
                        setStatusText();
                    }
                }
                else
                {
                    String oldzone = "";
                    if (selectedObj != null)
                        oldzone = selectedObj.zone;
                       
                    selectedVal = objid;
                    selectedObj = globalObjList.get(objid);
                    
                    if (!oldzone.isEmpty() && !oldzone.equals(selectedObj.zone))
                        rerenderTasks.add("zone:"+oldzone);

                    if (galaxyMode)
                    {
                        for (int z = 0; z < galaxyArc.zoneList.size(); z++)
                        {
                            if (!galaxyArc.zoneList.get(z).equals(selectedObj.zone))
                                continue;
                            lbZoneList.setSelectedIndex(z);
                            break;
                        }
                    }
                    tpLeftPanel.setSelectedIndex(1);

                    TreeNode objnode = ((DefaultMutableTreeNode)tvObjectList.getModel().getRoot()).getChildAt(0);
                    ObjTreeNode finalnode = (ObjTreeNode)((ObjListTreeNode)objnode).children.get(objid);
                    TreePath tp = new TreePath(((DefaultTreeModel)tvObjectList.getModel()).getPathToRoot(finalnode));
                    tvObjectList.setSelectionPath(tp);
                    tvObjectList.scrollPathToVisible(tp);
                }
            }
            
            e.getComponent().repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            if (!inited) return;
            
            if (mouseButton == MouseEvent.BUTTON1 && selectedObj != null && selectedVal == underCursor)
            {
                float delta = (float)e.getPreciseWheelRotation();
                delta = ((delta < 0f) ? -1f:1f) * (float)Math.pow(delta, 2f) * 0.05f * scaledown;
                
                Vector3 vdelta = new Vector3(
                        delta * (float)Math.cos(camRotation.x) * (float)Math.cos(camRotation.y),
                        delta * (float)Math.sin(camRotation.y),
                        delta * (float)Math.sin(camRotation.x) * (float)Math.cos(camRotation.y));
                
                float xdist = delta * (lastMouseMove.x - (glCanvas.getWidth() / 2f)) * pixelFactorX;
                float ydist = delta * (lastMouseMove.y - (glCanvas.getHeight() / 2f)) * pixelFactorY;
                vdelta.x += -(xdist * (float)Math.sin(camRotation.x)) - (ydist * (float)Math.sin(camRotation.y) * (float)Math.cos(camRotation.x));
                vdelta.y += ydist * (float)Math.cos(camRotation.y);
                vdelta.z += (xdist * (float)Math.cos(camRotation.x)) - (ydist * (float)Math.sin(camRotation.y) * (float)Math.sin(camRotation.x));
                
                applySubzoneRotation(vdelta);

                selectedObj.position.x += vdelta.x;
                selectedObj.position.y += vdelta.y;
                selectedObj.position.z += vdelta.z;

                pnlObjectSettings.setFieldValue("pos_x", selectedObj.position.x);
                pnlObjectSettings.setFieldValue("pos_y", selectedObj.position.y);
                pnlObjectSettings.setFieldValue("pos_z", selectedObj.position.z);
                rerenderTasks.add("zone:"+selectedObj.zone);
            }
            else
            {
                float delta = (float)(e.getPreciseWheelRotation() * 0.1f);
                camTarget.x += delta * (float)Math.cos(camRotation.x) * (float)Math.cos(camRotation.y);
                camTarget.y += delta * (float)Math.sin(camRotation.y);
                camTarget.z += delta * (float)Math.sin(camRotation.x) * (float)Math.cos(camRotation.y);

                updateCamera();
            }
            
            pickingCapture = true;
            e.getComponent().repaint();
        }
        
        
        public final float fov = (float)((70f * Math.PI) / 180f);
        public final float zNear = 0.01f;
        public final float zFar = 1000f;
    }
    
    public final float scaledown = 10000f;
    
    public boolean galaxyMode;
    public String galaxyName;
    public GalaxyEditorForm parentForm;
    public HashMap<String, GalaxyEditorForm> childZoneEditors;
    public GalaxyArchive galaxyArc;
    public GalaxyRenderer renderer;
    public HashMap<String, ZoneArchive> zoneArcs;
    
    public int curScenarioID;
    public Bcsv.Entry curScenario;
    public String curZone;
    public ZoneArchive curZoneArc;
    
    public int maxUniqueID;
    public HashMap<Integer, LevelObject> globalObjList;
    
    public class SubZoneData
    {
        String layer;
        Vector3 position;
        Vector3 rotation;
    }
    public HashMap<String, SubZoneData> subZoneData;
    
    private GLCanvas glCanvas;
    private boolean inited;
        
    private GLRenderer.RenderInfo renderinfo;
    private HashMap<String, int[]> objDisplayLists;
    private HashMap<Integer, int[]> zoneDisplayLists;
    private Queue<String> rerenderTasks;
    private int zoneModeLayerBitmask;

    private Matrix4 modelViewMatrix;
    private float camDistance;
    private Vector2 camRotation;
    private Vector3 camPosition, camTarget;
    private boolean upsideDown;
    private float pixelFactorX, pixelFactorY;

    private int mouseButton;
    private Point lastMouseMove;
    private boolean isDragging;
    private boolean pickingCapture;
    private IntBuffer pickingFrameBuffer;
    private FloatBuffer pickingDepthBuffer;
    private float pickingDepth;

    private int underCursor;
    private float depthUnderCursor;
    private int selectedVal;
    private LevelObject selectedObj;
    private String objectBeingAdded, addingOnLayer;
    private boolean deletingObjects;
    
    private CheckBoxList lbLayersList;
    private PropertyPanel pnlObjectSettings;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddScenario;
    private javax.swing.JButton btnAddZone;
    private javax.swing.JButton btnDeleteScenario;
    private javax.swing.JButton btnDeleteZone;
    private javax.swing.JButton btnDeselect;
    private javax.swing.JButton btnEditScenario;
    private javax.swing.JButton btnEditZone;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JList lbScenarioList;
    private javax.swing.JLabel lbSelected;
    private javax.swing.JLabel lbStatusLabel;
    private javax.swing.JList lbZoneList;
    private javax.swing.JPanel pnlGLPanel;
    private javax.swing.JPanel pnlLayersPanel;
    private javax.swing.JSplitPane pnlScenarioZonePanel;
    private javax.swing.JScrollPane scpLayersList;
    private javax.swing.JScrollPane scpObjSettingsContainer;
    private javax.swing.JToggleButton tgbAddObject;
    private javax.swing.JToggleButton tgbDeleteObject;
    private javax.swing.JTabbedPane tpLeftPanel;
    private javax.swing.JTree tvObjectList;
    // End of variables declaration//GEN-END:variables
}
