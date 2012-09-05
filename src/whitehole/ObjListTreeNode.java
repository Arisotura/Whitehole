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
import java.util.LinkedHashMap;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import whitehole.smg.LevelObject;

public class ObjListTreeNode implements MutableTreeNode
{
    public ObjListTreeNode()
    {
        parent = null;
        children = new LinkedHashMap<>();
    }
    
    @Override
    public void insert(MutableTreeNode child, int index)
    {
        throw new UnsupportedOperationException("This is not how you add nodes to this kind of TreeNode.");
    }

    @Override
    public void remove(int index)
    {
        int key = (int)children.keySet().toArray()[index];
        children.remove(key);
    }

    @Override
    public void remove(MutableTreeNode node)
    {
        children.remove(((ObjTreeNode)node).object.uniqueID);
    }

    @Override
    public void setUserObject(Object object)
    {
        userObject = object;
    }

    @Override
    public void removeFromParent()
    {
        parent = null;
        System.out.println("[ObjListTreeNode] REMOVE FROM PARENT");
    }

    @Override
    public void setParent(MutableTreeNode newParent)
    {
        parent = newParent;
    }

    @Override
    public TreeNode getChildAt(int childIndex)
    {
        return (TreeNode)children.values().toArray()[childIndex];
    }

    @Override
    public int getChildCount()
    {
        return children.size();
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node)
    {
        int uid = ((ObjTreeNode)node).object.uniqueID;
        int i = 0;
        for (TreeNode tn : children.values())
        {
            if (((ObjTreeNode)tn).object.uniqueID == uid)
                return i;
            
            i++;
        }
        
        return -1;
    }

    @Override
    public boolean getAllowsChildren()
    {
        return true;
    }

    @Override
    public boolean isLeaf()
    {
        return children.isEmpty();
    }

    @Override
    public Enumeration children()
    {
        return new Iterator2Enumeration(children.values().iterator());
    }
    
    
    @Override
    public String toString()
    {
        return userObject.toString();
    }
    
    
    public TreeNode addObject(LevelObject obj)
    {
        ObjTreeNode tn = new ObjTreeNode(obj);
        children.put(obj.uniqueID, tn);
        tn.setParent(this);
        return tn;
    }
    
    
    Object userObject;
    TreeNode parent;
    LinkedHashMap<Integer, TreeNode> children;
}
