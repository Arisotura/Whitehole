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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class PropertyGrid extends JTable
{
    public PropertyGrid(JFrame parent)
    {
        this.parent = parent;
        this.setModel(new PGModel());
        this.setUI(new PGUI());
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
    
    @Override
    public Rectangle getCellRect(int row, int col, boolean includeSpacing)
    {
        Rectangle lolrect = super.getCellRect(row, col, includeSpacing);
        System.out.println("macarena "+col+" - "+row);
        if (row == 0 && col == 0)
            lolrect.width *= 2;
        else if (row == 0 && col == 1)
            lolrect.width = 0;
        
        return lolrect;
    }
    
    @Override
    public TableCellRenderer getCellRenderer(int row, int col)
    {
        /*if (row == 0)
        {
            return new TableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    if (column == 1) return null;
                    
                    JLabel lol =  new JLabel("this is a label");
                    
                    lol.setSize(200, lol.getSize().height);
                    lol.setMinimumSize(new Dimension(200, lol.getSize().height));
                    
                    return lol;
                }
            };
        }*/
        
        return super.getCellRenderer(row, col);
    }
    
    @Override
    public TableCellEditor getCellEditor(int row, int col)
    {
        return super.getCellEditor(row, col);
    }
    
    
    public class PGModel extends AbstractTableModel
    {
        @Override
        public int getRowCount()
        {
            return 10;
        }

        @Override
        public int getColumnCount()
        {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            return "lol"+rowIndex;
        }
        
        @Override
        public String getColumnName(int col)
        {
            if (col == 0) return "Property";
            return "Value";
        }
        
        @Override
        public boolean isCellEditable(int row, int col)
        {
            if (col == 0) return false;
            return true;
        }
    }
    
    // based off http://code.google.com/p/spantable/source/browse/SpanTable/src/main/java/spantable/SpanTableUI.java
    public class PGUI extends BasicTableUI 
    {
        @Override
        public void paint(Graphics g, JComponent c) 
        {
                Rectangle r = g.getClipBounds();
                int firstRow = table.rowAtPoint(new Point(r.x, r.y));
                int lastRow = table.rowAtPoint(new Point(r.x, r.y + r.height));
                // -1 is a flag that the ending point is outside the table:
                if (lastRow < 0)
                        lastRow = table.getRowCount() - 1;
                for (int row = firstRow; row <= lastRow; row++)
                        paintRow(row, g);
        }

        private void paintRow(int row, Graphics g) 
        {
                Rectangle clipRect = g.getClipBounds();
                for (int col = 0; col < table.getColumnCount(); col++) 
                {
                        Rectangle cellRect = table.getCellRect(row, col, true);
                        if (cellRect.intersects(clipRect)) 
                        {
                                // If a span is defined, only paint the active (top-left) cell. Otherwise paint the cell.
                                /*Span span = ((SpanTableModel)table.getModel()).getSpanModel().getDefinedSpan(row, col);
                                if ((span != null && span.isActive(row, col)) || span == null)*/ {
                                        // At least a part is visible.
                                        paintCell(row, col, g, cellRect);
                                }
                        }
                }
        }

        private void paintCell(int row, int column, Graphics g, Rectangle area) 
        {
                int verticalMargin = table.getRowMargin();
                int horizontalMargin = table.getColumnModel().getColumnMargin();

                Color c = g.getColor();
                g.setColor(table.getGridColor());
                // Acmlmboard border method
                g.drawLine(area.x+area.width-1, area.y, area.x+area.width-1, area.y+area.height-1);
                g.drawLine(area.x, area.y+area.height-1, area.x+area.width-1, area.y+area.height-1);
                g.setColor(c);

                area.setBounds(area.x + horizontalMargin / 2, area.y + verticalMargin / 2, 
                        area.width - horizontalMargin, area.height - verticalMargin);

                if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) 
                {
                        Component component = table.getEditorComponent();
                        component.setBounds(area);
                        component.validate();
                } 
                else 
                {
                        TableCellRenderer renderer = table.getCellRenderer(row, column);
                        Component component = table.prepareRenderer(renderer, row, column);
                        if (component.getParent() == null)
                                rendererPane.add(component);
                        rendererPane.paintComponent(g, component, table, area.x, area.y,
                                area.width, area.height, true);
                }
        }
    }

    
    
    private JFrame parent;
}
