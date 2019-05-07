package com.maxciv.infer.plugin.ui.tree

import com.intellij.ui.SimpleTextAttributes
import com.maxciv.infer.plugin.data.report.InferViolation
import com.maxciv.infer.plugin.ui.InferIcons.ICON_VIOLATION

import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class ViolationNode(var violation: InferViolation) : DefaultMutableTreeNode(), TreeNodeData {

    override fun render(cellRenderer: CellRenderer) {
        cellRenderer.append("(" + violation.line + ") ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        cellRenderer.append(violation.bugTypeHum + " – " + violation.qualifier, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        cellRenderer.icon = ICON_VIOLATION
        cellRenderer.toolTipText = violation.qualifier
    }
}
