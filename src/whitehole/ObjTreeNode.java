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

import java.util.Enumeration;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import whitehole.smg.LevelObject;

public class ObjTreeNode implements MutableTreeNode
{
    public ObjTreeNode(LevelObject obj)
    {
        this.parent = null;
        this.object = obj;
    }
    

    @Override
    public void insert(MutableTreeNode child, int index) {}
    @Override
    public void remove(int index) {}
    @Override
    public void remove(MutableTreeNode node) {}

    @Override
    public void setUserObject(Object object) {}

    @Override
    public void removeFromParent()
    {
        parent = null;
        System.out.println("[ObjTreeNode] REMOVE FROM PARENT");
    }

    @Override
    public void setParent(MutableTreeNode newParent)
    {
        parent = newParent;
    }

    @Override
    public TreeNode getChildAt(int childIndex)
    {
        return null;
    }

    @Override
    public int getChildCount()
    {
        return 0;
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node)
    {
        return -1;
    }

    @Override
    public boolean getAllowsChildren()
    {
        return false;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public Enumeration children()
    {
        return null;
    }
    
    
    @Override
    public String toString()
    {
        String layer = object.layer.equals("common") ? "Common" : "Layer"+object.layer.substring(5).toUpperCase();
        return object.dbInfo.name + " [" + layer + "]";
    }
    
    
    public TreeNode parent;
    public LevelObject object;
}
