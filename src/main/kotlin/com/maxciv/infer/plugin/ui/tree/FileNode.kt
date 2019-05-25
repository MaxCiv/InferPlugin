package com.maxciv.infer.plugin.ui.tree

import com.intellij.ui.SimpleTextAttributes
import icons.InferIcons.ICON_CLASS
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class FileNode(
    var file: String,
    var violationsCount: Int,
    var isShortClassName: Boolean
) : DefaultMutableTreeNode(), TreeNodeData {

    override fun render(cellRenderer: CellRenderer) =
        if (isShortClassName) shortFilePath(cellRenderer) else fullFilePath(cellRenderer)

    private fun fullFilePath(cellRenderer: CellRenderer) = with(cellRenderer) {
        file.split(File.separator).last().also { className ->
            append(file.replace(className, ""), SimpleTextAttributes.REGULAR_ATTRIBUTES)
            append(className.replace(".java", ""), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
            append(".java", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        if (violationsCount > 0) append(" ($violationsCount violations) ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        icon = ICON_CLASS
    }

    private fun shortFilePath(cellRenderer: CellRenderer) = with(cellRenderer) {
        file.split(File.separator).last().also { className ->
            append(className.replace(".java", ""), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
            append(".java", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        }
        if (violationsCount > 0) append(" ($violationsCount violations) ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        icon = ICON_CLASS
    }
}
