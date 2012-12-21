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

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;
import javax.swing.event.*;

public class PropertyPanel extends JPanel
{
    public PropertyPanel()
    {
        super();
        setLayout(new GridBagLayout());
        
        categories = new LinkedHashMap<>();
        fields = new LinkedHashMap<>();
        
        curRow = 0; curIndex = 0;
        curCategory = null;
        
        eventListener = null;
    }
    
    
    public void setEventListener(EventListener listener)
    {
        eventListener = listener;
    }
    
    public void clear()
    {
        this.removeAll();
        
        categories.clear();
        fields.clear();
        
        curRow = 0; curIndex = 0;
        curCategory = null;
    }
    
    public void addCategory(String name, String caption)
    {
        Category cat = new Category();
        categories.put(name, cat);
        
        cat.name = name;
        cat.caption = caption;
        cat.startRow = curRow;
        cat.endRow = curRow;
        cat.startIndex = curIndex;
        cat.endIndex = curIndex;
        
        curCategory = cat;
        
        JToggleButton btn = new JToggleButton("[-] " + cat.caption);
        cat.header = btn;
        btn.setSelected(true);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        add(btn, new GridBagConstraints(0, curRow, 3, 1, 1f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
        btn.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent evt) 
            {
                for (Category c : categories.values())
                {
                    if (!c.header.equals(evt.getSource()))
                        continue;
                    
                    if (!c.header.isSelected())
                    {
                        c.header.setText("[+] " + c.caption);
                        
                        for (int i = c.startIndex + 1; i <= c.endIndex; i++)
                            getComponent(i).setVisible(false);
                    }
                    else
                    {
                        c.header.setText("[-] " + c.caption);
                        
                        for (int i = c.startIndex + 1; i <= c.endIndex; i++)
                            getComponent(i).setVisible(true);
                    }
                    
                    break;
                }
            }
        });
        
        curRow++;
        curIndex++;
    }
    
    public void addField(String name, String caption, String type, java.util.List choices, Object val)
    {
        if (curCategory == null)
            throw new NullPointerException("You must add a category before adding fields.");
        
        Field field = new Field();
        fields.put(name, field);
        
        field.name = name;
        field.caption = caption;
        field.type = type;
        
        field.row = curRow;
        field.index = curIndex;
        
        add(new JLabel(field.caption+":"), new GridBagConstraints(0, curRow, 1, 1, 0.4f, 0f, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,0,1), 0, 0));
        curIndex++;
        
        switch (field.type)
        {
            case "text":
            case "int":
                field.field = new JTextField(val.toString());
                ((JTextField)field.field).addKeyListener(new KeyListener()
                {
                    public void keyPressed(KeyEvent evt) {}
                    public void keyTyped(KeyEvent evt) {}
                    public void keyReleased(KeyEvent evt)
                    {
                        for (Field field : fields.values())
                        {
                            if (!field.field.equals(evt.getSource())) continue;
                            String val = ((JTextField)evt.getSource()).getText();
                            try
                            {
                                if (!field.type.equals("text")) val = String.format("%1$d", Long.parseLong(val));
                                ((JTextField)evt.getSource()).setForeground(Color.getColor("text"));
                            }
                            catch (NumberFormatException ex)
                            {
                                val = "0";
                                ((JTextField)evt.getSource()).setForeground(new Color(0xFF4040));
                            }
                            eventListener.propertyChanged(field.name, val);
                            break;
                        }
                    }
                });
                
                add(field.field, new GridBagConstraints(1, curRow, 2, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
                
                
            case "float":
                field.field = new JSpinner();
                ((JSpinner)field.field).setModel(new SpinnerNumberModel((float)val, -Float.MAX_VALUE, Float.MAX_VALUE, 1f));
                field.field.setPreferredSize(new Dimension(10, field.field.getMinimumSize().height));
                ((JSpinner)field.field).addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent evt)
                    {
                        for (Field field : fields.values())
                        {
                            if (!field.field.equals(evt.getSource())) continue;
                            eventListener.propertyChanged(field.name, ((JSpinner)evt.getSource()).getValue());
                            break;
                        }
                    }
                });
                
                add(field.field, new GridBagConstraints(1, curRow, 2, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
                
            case "list":
                field.field = new JComboBox();
                for (Object item : choices)
                    ((JComboBox)field.field).addItem(item);
                ((JComboBox)field.field).setSelectedItem(val);
                ((JComboBox)field.field).addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        for (Field field : fields.values())
                        {
                            if (!field.field.equals(evt.getSource())) continue;
                            eventListener.propertyChanged(field.name, ((JComboBox)evt.getSource()).getSelectedItem());
                            break;
                        }
                    }
                });
                
                add(field.field, new GridBagConstraints(1, curRow, 2, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
                
            case "bool":
                field.field = new JCheckBox();
                ((JCheckBox)field.field).setText(" ");
                ((JCheckBox)field.field).setSelected((boolean)val);
                ((JCheckBox)field.field).addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        for (Field field : fields.values())
                        {
                            if (!field.field.equals(evt.getSource())) continue;
                            eventListener.propertyChanged(field.name, ((JCheckBox)evt.getSource()).isSelected());
                            break;
                        }
                    }
                });
                
                add(field.field, new GridBagConstraints(1, curRow, 2, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
                
                
            case "objname":
                field.field = new JTextField(val.toString());
                ((JTextField)field.field).addKeyListener(new KeyListener()
                {
                    public void keyPressed(KeyEvent evt) {}
                    public void keyTyped(KeyEvent evt) {}
                    public void keyReleased(KeyEvent evt)
                    {
                        for (Field field : fields.values())
                        {
                            if (!field.field.equals(evt.getSource())) continue;
                            eventListener.propertyChanged(field.name, ((JTextField)evt.getSource()).getText());
                            break;
                        }
                    }
                });
                
                add(field.field, new GridBagConstraints(1, curRow, 1, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                
                JButton extrabtn = new JButton("...");
                extrabtn.setPreferredSize(new Dimension(extrabtn.getMinimumSize().height, extrabtn.getMinimumSize().height));
                extrabtn.addActionListener(new ActionListener() 
                {
                    public void actionPerformed(ActionEvent evt) 
                    {
                        // warning: black magic inside
                        
                        Component form = (Component)evt.getSource();
                        do form = (Component)form.getParent();
                        while (form.getClass() != GalaxyEditorForm.class);
                        GalaxyEditorForm gform = (GalaxyEditorForm)form;
                        
                        PropertyPanel panel = (PropertyPanel)((Component)evt.getSource()).getParent();
                        int index = 0;
                        for (Component c : panel.getComponents())
                        {
                            if (c.equals(evt.getSource())) break;
                            index++;
                        }
                        JTextField field = (JTextField)panel.getComponents()[index-1];
                        
                        ObjectSelectForm objsel = new ObjectSelectForm(gform, gform.zoneArcs.get(gform.galaxyName).gameMask, field.getText());
                        objsel.setVisible(true);
                        
                        field.setText(objsel.selectedObject);
                        for (Field f : fields.values())
                        {
                            if (!f.field.equals(field)) continue;
                            eventListener.propertyChanged(f.name, objsel.selectedObject);
                            break;
                        }
                    }
                });
                
                add(extrabtn, new GridBagConstraints(2, curRow, 1, 1, 0f, 0f, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
        }
        
        curCategory.endRow = curRow;
        curCategory.endIndex = curIndex - 1;
        
        curRow++;
    }
    
    public void addTermination()
    {
        add(Box.createVerticalGlue(), 
                new GridBagConstraints(0, curRow, 3, 1, 1f, 1f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        
        curRow++;
        curIndex++;
    }
    
    
    public void setFieldValue(String field, Object value)
    {
        Field f = fields.get(field);
        
        switch (f.type)
        {
            case "text": 
            case "objname": ((JTextField)f.field).setText((String)value); break;
            case "int": ((JTextField)f.field).setText(String.format("%1$d", Long.parseLong((String)value))); break;
            case "float": ((JSpinner)f.field).setValue((double)(float)value); break;
            case "bool": ((JCheckBox)f.field).setSelected((boolean)value); break;
        }
    }
    
    
    public class Category
    {
        String name;
        
        String caption;
        int startRow, endRow;
        int startIndex, endIndex;
        JToggleButton header;
    }
    
    public class Field
    {
        String name;
        
        String caption;
        String type;
        int row, index;
        Component field;
    }
    
    public interface EventListener 
    {
        public void propertyChanged(String propname, Object value);
    }
    
    
    public LinkedHashMap<String, Category> categories;
    public LinkedHashMap<String, Field> fields;
    
    private int curRow, curIndex;
    private Category curCategory;
    
    private EventListener eventListener;
}
