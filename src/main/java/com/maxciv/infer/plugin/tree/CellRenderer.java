package com.maxciv.infer.plugin.tree;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class CellRenderer extends ColoredTreeCellRenderer {

    static final Icon ICON = IconLoader.getIcon("/icons/testPassed.png");

    public CellRenderer() {
        super();
    }

    @Override
    public void customizeCellRenderer(
            @NotNull JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus
    ) {
        if (value instanceof TreeNodeData) {
            ((TreeNodeData) value).render(this);
        }
    }
}
