package com.maxciv.infer.plugin.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.process.report.InferReport
import com.maxciv.infer.plugin.tree.CellRenderer
import com.maxciv.infer.plugin.tree.RootNode
import com.maxciv.infer.plugin.tree.TreeNodeFactory
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class ResultsTab(project: Project) : JPanel(BorderLayout()) {
    private val pluginSettings: InferPluginSettings? = project.getComponent(InferProjectComponent::class.java).pluginSettings
    private val treeResults: Tree
    private var rootNode: RootNode? = null

    init {
        this.treeResults = createNewTree()
        add(JBScrollPane(treeResults), BorderLayout.CENTER)
        project.getComponent(InferProjectComponent::class.java).resultsTab = this
    }

    private fun createNewTree(): Tree {
        val newTree = Tree()
        rootNode = TREE_NODE_FACTORY.createDefaultRootNode() as RootNode
        val treeModel = DefaultTreeModel(rootNode)
        newTree.model = treeModel
        newTree.cellRenderer = CellRenderer()
        return newTree
    }

    fun fillTreeFromResult(inferReport: InferReport) {
        rootNode!!.removeAllChildren()
        rootNode!!.inferReport = inferReport
        if (!inferReport.violations!!.isEmpty()) {
            inferReport.violations!!.forEach { violation -> addNode(TREE_NODE_FACTORY.createNode(violation)) }
        } else {
            addNode(TREE_NODE_FACTORY.createNode("No violations found."))
        }
    }

    private fun addNode(node: DefaultMutableTreeNode?): DefaultMutableTreeNode? {
        return addNode(rootNode!!, node)
    }

    private fun addNode(parent: DefaultMutableTreeNode, node: DefaultMutableTreeNode?): DefaultMutableTreeNode? {
        parent.add(node)
        ApplicationManager.getApplication().invokeLater { (treeResults.model as DefaultTreeModel).reload() }
        return node
    }

    companion object {
        private val TREE_NODE_FACTORY = TreeNodeFactory.instance
    }
}
