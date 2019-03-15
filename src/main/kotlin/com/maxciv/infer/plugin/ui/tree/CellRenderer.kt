package com.maxciv.infer.plugin.ui.tree

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.ColoredTreeCellRenderer
import javax.swing.JTree

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class CellRenderer : ColoredTreeCellRenderer() {

    override fun customizeCellRenderer(
            tree: JTree,
            value: Any,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
    ) {
        if (value is TreeNodeData) {
            value.render(this)
        }
    }

    companion object {
        internal val ICON = IconLoader.getIcon("/icons/testPassed.png")
    }
}
