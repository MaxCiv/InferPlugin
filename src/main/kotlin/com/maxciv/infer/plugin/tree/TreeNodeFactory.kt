package com.maxciv.infer.plugin.tree

import com.maxciv.infer.plugin.process.report.InferReport
import com.maxciv.infer.plugin.process.report.InferViolation

import javax.swing.tree.DefaultMutableTreeNode

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class TreeNodeFactory private constructor() {

    fun createNode(userObject: Any): DefaultMutableTreeNode? {
        if (userObject is InferViolation) {
            return ViolationNode(userObject)
        } else if (userObject is InferReport) {
            return RootNode(userObject)
        } else if (userObject is String) {
            return StringNode(userObject)
        }
        return null
    }

    fun createNode(violation: InferViolation): DefaultMutableTreeNode {
        return ViolationNode(violation)
    }

    fun createDefaultRootNode(): DefaultMutableTreeNode {
        return RootNode()
    }

    companion object {
        val instance = TreeNodeFactory()
    }
}
