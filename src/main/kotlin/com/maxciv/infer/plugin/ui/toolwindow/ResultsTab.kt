package com.maxciv.infer.plugin.ui.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.ui.tree.CellRenderer
import com.maxciv.infer.plugin.ui.tree.RootNode
import com.maxciv.infer.plugin.ui.tree.TreeNodeFactory
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class ResultsTab(project: Project) : JPanel(BorderLayout()) {

    private val pluginSettings: InferPluginSettings =
        project.getComponent(InferProjectComponent::class.java).pluginSettings!!
    private val treeResults: Tree
    private var rootNode: RootNode = TreeNodeFactory.createDefaultRootNode() as RootNode

    init {
        treeResults = Tree().apply {
            model = DefaultTreeModel(rootNode)
            cellRenderer = CellRenderer()
        }
        add(JBScrollPane(treeResults), BorderLayout.CENTER)
        project.getComponent(InferProjectComponent::class.java).resultsTab = this
    }

    fun fillTreeFromResult(inferReport: InferReport) {
        rootNode.removeAllChildren()
        rootNode.inferReport = inferReport

        if (inferReport.violationsByFile.isNotEmpty()) {
            inferReport.violationsByFile.forEach { (file, violations) ->
                addNodeToRoot(TreeNodeFactory.createFileNode(file, violations.count())).also { fileNode ->
                    violations.forEach { addNodeToParent(fileNode, TreeNodeFactory.createNode(it)) }
                }
            }
        } else {
            addNodeToRoot(TreeNodeFactory.createNode("No violations found."))
        }
    }

    private fun addNodeToRoot(node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        return addNodeToParent(rootNode, node)
    }

    private fun addNodeToParent(parent: DefaultMutableTreeNode, node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        parent.add(node)
        ApplicationManager.getApplication().invokeLater { (treeResults.model as DefaultTreeModel).reload() }
        return node
    }
}
