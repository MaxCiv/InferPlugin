package com.maxciv.infer.plugin.ui.toolwindow

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.actions.ActionGroups
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.onsave.OnSaveAnalyzeListener
import com.maxciv.infer.plugin.ui.tree.CellRenderer
import com.maxciv.infer.plugin.ui.tree.RootNode
import com.maxciv.infer.plugin.ui.tree.TreeNodeFactory
import com.maxciv.infer.plugin.ui.tree.ViolationNode
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import kotlin.math.max



/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class ResultsTab(private val project: Project) : JPanel(BorderLayout()) {

    private val pluginSettings: InferPluginSettings =
        project.getComponent(InferProjectComponent::class.java).pluginSettings
    val treeResults: Tree
    private var rootNode: RootNode = TreeNodeFactory.createDefaultRootNode() as RootNode

    init {
        VirtualFileManager.getInstance().addVirtualFileListener(OnSaveAnalyzeListener(project))

        val toolWindowActionGroup = ActionManager.getInstance().getAction(ActionGroups.RESULTS_TAB.id) as ActionGroup
        val toolWindowToolbar =
            ActionManager.getInstance().createActionToolbar("Results", toolWindowActionGroup, false)
        toolWindowToolbar.component.isVisible = true
        add(toolWindowToolbar.component, BorderLayout.WEST)

        treeResults = Tree().apply {
            model = DefaultTreeModel(rootNode)
            cellRenderer = CellRenderer()
        }
        add(JBScrollPane(treeResults), BorderLayout.CENTER)

        treeResults.addMouseListener(MouseClickListener())
        project.getComponent(InferProjectComponent::class.java).resultsTab = this

        fillTreeFromResult(pluginSettings.aggregatedInferReport)
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
        ApplicationManager.getApplication().invokeLater { TreeUtil.expandAll(treeResults) }
        DaemonCodeAnalyzer.getInstance(project).restart()
    }

    private fun addNodeToRoot(node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        return addNodeToParent(rootNode, node)
    }

    private fun addNodeToParent(parent: DefaultMutableTreeNode, node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        parent.add(node)
        ApplicationManager.getApplication().invokeLater { (treeResults.model as DefaultTreeModel).reload() }
        return node
    }

    private fun openEditor(violationNode: ViolationNode) {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(
            project.basePath!! + "/" + violationNode.violation.file.replace(File.separatorChar, '/')
        ) ?: return

        fileEditorManager.openTextEditor(
            OpenFileDescriptor(
                project,
                virtualFile,
                max(violationNode.violation.line - 1, 0),
                max(violationNode.violation.column - 1, 0)
            ),
            true
        )
    }

    inner class MouseClickListener : MouseAdapter() {
        //Get the current tree node where the mouse event happened
        private val nodeFromEvent: DefaultMutableTreeNode?
            get() {
                if (treeResults.selectionPaths == null || treeResults.selectionPaths.isEmpty()) return null
                return treeResults.selectionPaths[0].lastPathComponent as DefaultMutableTreeNode
            }

        override fun mousePressed(mouseEvent: MouseEvent) {
            if (nodeFromEvent is ViolationNode && (mouseEvent.clickCount == 2 || pluginSettings.isAutoscrollToSourceEnabled)) {
                openEditor(nodeFromEvent as ViolationNode)
            }
        }
    }
}
