package com.maxciv.infer.plugin.ui.tree

import com.intellij.ui.SimpleTextAttributes
import com.maxciv.infer.plugin.data.report.InferReport
import icons.InferIcons.ICON_INFER

import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class RootNode : DefaultMutableTreeNode(LABEL), TreeNodeData {

    lateinit var inferReport: InferReport

    override fun render(cellRenderer: CellRenderer) {
        cellRenderer.append(LABEL, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        if (this::inferReport.isInitialized) {
            cellRenderer.append(
                " (${inferReport.violationsByFile.size} files with ${inferReport.getTotalViolationCount()} violations)",
                SimpleTextAttributes.GRAYED_ATTRIBUTES
            )
        }
        cellRenderer.icon = ICON_INFER
    }

    companion object {
        private const val LABEL = "Infer Results"
    }
}
