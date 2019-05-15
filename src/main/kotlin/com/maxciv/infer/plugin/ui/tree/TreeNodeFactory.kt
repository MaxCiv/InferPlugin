package com.maxciv.infer.plugin.ui.tree

import com.maxciv.infer.plugin.data.report.InferViolation
import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
object TreeNodeFactory {

    fun createNode(userObject: Any): DefaultMutableTreeNode {
        return when (userObject) {
            is InferViolation -> ViolationNode(userObject)
            is String -> StringNode(userObject)
            else -> StringNode("Unknown node type")
        }
    }

    fun createFileNode(file: String, violationsCount: Int, isShortClassName: Boolean): DefaultMutableTreeNode {
        return FileNode(file, violationsCount, isShortClassName)
    }

    fun createDefaultRootNode(): DefaultMutableTreeNode {
        return RootNode()
    }
}
