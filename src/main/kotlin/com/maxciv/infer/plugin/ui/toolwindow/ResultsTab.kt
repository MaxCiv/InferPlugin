package com.maxciv.infer.plugin.ui.toolwindow

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.fileEditor.FileEditorManagerListener.FILE_EDITOR_MANAGER
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.tabs.TabInfo
import com.intellij.ui.tabs.impl.JBTabsImpl
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.actions.ActionGroups
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.process.onsave.OnSaveAnalyzeListener
import com.maxciv.infer.plugin.toProjectRelativePath
import com.maxciv.infer.plugin.ui.tree.*
import icons.InferIcons.ICON_FULL_REPORT
import icons.InferIcons.ICON_REPORT_CURRENT_FILE
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

    private val inferProjectComponent: InferProjectComponent = project.getComponent(InferProjectComponent::class.java)
    private val pluginSettings: InferPluginSettings = inferProjectComponent.pluginSettings

    private var currentFilename: String = ""

    private val currentFileTab: TabInfo
    private val currentFileTreeResults: Tree
    private var currentFileRootNode: FileNode =
        TreeNodeFactory.createFileNode("Current file", 0, pluginSettings.isShortClassNamesEnabled) as FileNode

    private val fullReportTab: TabInfo
    private val fullReportTreeResults: Tree
    private var fullReportRootNode: RootNode = TreeNodeFactory.createDefaultRootNode() as RootNode

    init {
        val toolWindowActionGroup = ActionManager.getInstance().getAction(ActionGroups.RESULTS_TAB.id) as ActionGroup
        val toolWindowToolbar =
            ActionManager.getInstance().createActionToolbar("Results", toolWindowActionGroup, false)
        toolWindowToolbar.component.isVisible = true
        add(toolWindowToolbar.component, BorderLayout.WEST)

        currentFileTreeResults = Tree().apply {
            model = DefaultTreeModel(currentFileRootNode)
            cellRenderer = CellRenderer()
        }
        fullReportTreeResults = Tree().apply {
            model = DefaultTreeModel(fullReportRootNode)
            cellRenderer = CellRenderer()
        }

        currentFileTab = TabInfo(JBScrollPane(currentFileTreeResults)).apply {
            text = "Current file"
            icon = ICON_REPORT_CURRENT_FILE
        }
        fullReportTab = TabInfo(JBScrollPane(fullReportTreeResults)).apply {
            text = "Full report"
            icon = ICON_FULL_REPORT
        }

        val tabs = JBTabsImpl(project).apply {
            addTab(currentFileTab)
            addTab(fullReportTab)
        }
        add(tabs, BorderLayout.CENTER)

        currentFileTreeResults.addMouseListener(CurrentFileMouseClickListener())
        fullReportTreeResults.addMouseListener(FullReportMouseClickListener())
        inferProjectComponent.resultsTab = this

        updateFullReportTree()
        updateCurrentFileTree(true)

        project.messageBus.connect().subscribe(FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
                val newFile = event.newFile ?: return
                val filename = newFile.canonicalPath!!.toProjectRelativePath(project.basePath!!)
                updateCurrentFileTree(filename)
            }
        })

        VirtualFileManager.getInstance().addVirtualFileListener(OnSaveAnalyzeListener(project))
    }

    fun updateCurrentFileTree(findCurrentFile: Boolean = false) {
        val filename = if (findCurrentFile) {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
            val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return
            virtualFile.canonicalPath!!.toProjectRelativePath(project.basePath!!)
        } else currentFilename

        updateCurrentFileTree(filename)
    }

    fun updateCurrentFileTree(filename: String) {
        if (!filename.endsWith(".java")) return

        currentFilename = filename
        currentFileRootNode.removeAllChildren()
        currentFileRootNode.file = filename
        currentFileRootNode.isShortClassName = pluginSettings.isShortClassNamesEnabled

        currentFileTab.text = "Current file"
        val violations = pluginSettings.aggregatedInferReport.violationsByFile.getOrDefault(filename, listOf())
        violations.also {
            if (it.count() > 0) {
                currentFileTab.append(" (${it.count()} violations)", SimpleTextAttributes.SYNTHETIC_ATTRIBUTES)
            } else {
                currentFileTab.append(" (${it.count()} violations)", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                addNodeToParent(currentFileRootNode, TreeNodeFactory.createNode("All clear!"))
            }
        }
            .forEach { violation ->
                addNodeToParent(currentFileRootNode, TreeNodeFactory.createNode(violation))
            }
        ApplicationManager.getApplication().invokeLater {
            (currentFileTreeResults.model as DefaultTreeModel).reload()
            TreeUtil.expandAll(currentFileTreeResults)
        }
        DaemonCodeAnalyzer.getInstance(project).restart()
    }

    fun updateFullReportTree() {
        fullReportRootNode.removeAllChildren()
        fullReportRootNode.inferReport = pluginSettings.aggregatedInferReport

        if (pluginSettings.aggregatedInferReport.violationsByFile.isNotEmpty()) {
            pluginSettings.aggregatedInferReport.violationsByFile.forEach { (file, violations) ->
                addNodeToRoot(
                    TreeNodeFactory.createFileNode(
                        file,
                        violations.count(),
                        pluginSettings.isShortClassNamesEnabled
                    )
                ).also { fileNode ->
                    violations.forEach { addNodeToParent(fileNode, TreeNodeFactory.createNode(it)) }
                }
            }
        } else {
            addNodeToRoot(TreeNodeFactory.createNode("No violations found."))
        }
        fullReportTab.text = "Full report"
        if (pluginSettings.aggregatedInferReport.violationsByFile.isNotEmpty()) {
            fullReportTab.append(
                " (${pluginSettings.aggregatedInferReport.violationsByFile.count()} files / ${pluginSettings.aggregatedInferReport.getTotalViolationCount()} violations)",
                SimpleTextAttributes.GRAYED_ATTRIBUTES
            )
        }
        ApplicationManager.getApplication().invokeLater {
            (fullReportTreeResults.model as DefaultTreeModel).reload()
            TreeUtil.expandAll(fullReportTreeResults)
        }
        DaemonCodeAnalyzer.getInstance(project).restart()
    }

    private fun addNodeToRoot(node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        return addNodeToParent(fullReportRootNode, node)
    }

    private fun addNodeToParent(parent: DefaultMutableTreeNode, node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        parent.add(node)
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

    fun collapseFullReport() {
        ApplicationManager.getApplication().invokeLater {
            TreeUtil.collapseAll(fullReportTreeResults, 2)
        }
    }

    fun expandFullReport() {
        ApplicationManager.getApplication().invokeLater {
            TreeUtil.expandAll(fullReportTreeResults)
        }
    }

    inner class CurrentFileMouseClickListener : MouseAdapter() {
        //Get the current tree node where the mouse event happened
        private val nodeFromEvent: DefaultMutableTreeNode?
            get() {
                if (currentFileTreeResults.selectionPaths == null || currentFileTreeResults.selectionPaths.isEmpty()) return null
                return currentFileTreeResults.selectionPaths[0].lastPathComponent as DefaultMutableTreeNode
            }

        override fun mousePressed(mouseEvent: MouseEvent) {
            if (nodeFromEvent is ViolationNode
                && (mouseEvent.clickCount == 2 || pluginSettings.isAutoscrollToSourceEnabled)
            ) {
                openEditor(nodeFromEvent as ViolationNode)
            }
        }
    }

    inner class FullReportMouseClickListener : MouseAdapter() {
        //Get the current tree node where the mouse event happened
        private val nodeFromEvent: DefaultMutableTreeNode?
            get() {
                if (fullReportTreeResults.selectionPaths == null || fullReportTreeResults.selectionPaths.isEmpty()) return null
                return fullReportTreeResults.selectionPaths[0].lastPathComponent as DefaultMutableTreeNode
            }

        override fun mousePressed(mouseEvent: MouseEvent) {
            if (nodeFromEvent is ViolationNode
                && (mouseEvent.clickCount == 2 || pluginSettings.isAutoscrollToSourceEnabled)
            ) {
                openEditor(nodeFromEvent as ViolationNode)
            }
        }
    }
}
