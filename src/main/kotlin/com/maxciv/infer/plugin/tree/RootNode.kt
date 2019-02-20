package com.maxciv.infer.plugin.tree

import com.intellij.ui.SimpleTextAttributes
import com.maxciv.infer.plugin.process.report.InferReport

import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class RootNode : DefaultMutableTreeNode, TreeNodeData {

    var inferReport: InferReport? = null

    constructor() : super(LABEL) {}

    constructor(nodeName: String) : super(nodeName) {}

    internal constructor(inferReport: InferReport) {
        this.inferReport = inferReport
    }

    override fun render(cellRenderer: CellRenderer) {
        cellRenderer.append(LABEL, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        if (inferReport != null) {
            cellRenderer.append(" (" + inferReport!!.violations!!.size.toString() + " violations)",
                    SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }

    companion object {
        private const val LABEL = "Infer Results"
    }
}
