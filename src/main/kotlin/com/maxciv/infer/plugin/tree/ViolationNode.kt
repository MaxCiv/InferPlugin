package com.maxciv.infer.plugin.tree

import com.intellij.ui.SimpleTextAttributes
import com.maxciv.infer.plugin.process.report.InferViolation

import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class ViolationNode(var violation: InferViolation) : DefaultMutableTreeNode(), TreeNodeData {

    override fun render(cellRenderer: CellRenderer) {
        cellRenderer.append("(" + violation.line + ", " + violation.column + ") ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        cellRenderer.append(violation.bugTypeHum + " - " + violation.qualifier, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        cellRenderer.append(" (" + violation.file + ")", SimpleTextAttributes.GRAYED_ATTRIBUTES)
    }
}
