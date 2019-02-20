package com.maxciv.infer.plugin.tree

import com.intellij.ui.SimpleTextAttributes

import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class StringNode internal constructor(private val text: String) : DefaultMutableTreeNode(), TreeNodeData {

    override fun render(cellRenderer: CellRenderer) {
        cellRenderer.append(text, SimpleTextAttributes.REGULAR_ATTRIBUTES)
    }
}
