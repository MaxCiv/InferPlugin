package com.maxciv.infer.plugin.tree;

import com.intellij.ui.SimpleTextAttributes;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class StringNode extends DefaultMutableTreeNode implements TreeNodeData  {

    private String text;

    StringNode(String text) {
        this.text = text;
    }

    @Override
    public void render(CellRenderer cellRenderer) {
        cellRenderer.append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES);
    }
}
