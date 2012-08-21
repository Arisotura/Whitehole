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
import java.util.LinkedHashMap;
import javax.swing.*;

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
    
    public void addField(String name, String caption, String type, Object val)
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
                field.field = new JTextField(val.toString());
                
                add(field.field, new GridBagConstraints(1, curRow, 2, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
                
                
            case "float":
                field.field = new JSpinner();
                ((JSpinner)field.field).setModel(new SpinnerNumberModel((float)val, -Float.MAX_VALUE, Float.MAX_VALUE, 1f));
                field.field.setPreferredSize(new Dimension(10, field.field.getMinimumSize().height));
                
                add(field.field, new GridBagConstraints(1, curRow, 2, 1, 0.6f, 0f, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1,1,0,1), 0, 0));
                curIndex++;
                break;
                
                
            case "objname":
                field.field = new JTextField(val.toString());
                
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
    
    
    public LinkedHashMap<String, Category> categories;
    public LinkedHashMap<String, Field> fields;
    
    private int curRow, curIndex;
    private Category curCategory;
}
