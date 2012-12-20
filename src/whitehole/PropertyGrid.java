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

// PropertyGrid. Meant to replace PropertyPanel.
// TODO: program it here

package whitehole;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class PropertyGrid extends JTable
{
    public PropertyGrid(JFrame parent)
    {
        this.parent = parent;
        this.setModel(new PGModel());
    }
    
    
    public void clear()
    {
        //this.removeAll();
        
        /*categories.clear();
        fields.clear();
        
        curRow = 0; curIndex = 0;
        curCategory = null;*/
    }
    
    public void addCategory(String name, String caption)
    {
    }
    
    public void addField(String name, String caption, String type, java.util.List choices, Object val)
    {
    }
    
    
    public class PGModel extends AbstractTableModel
    {
        @Override
        public int getRowCount()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getColumnCount()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    
    private JFrame parent;
}
