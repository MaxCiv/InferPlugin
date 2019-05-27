package com.maxciv.infer.plugin.actions

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.maxciv.infer.plugin.InferProjectComponent

/**
 * @author maxim.oleynik
 * @since 24.04.2019
 */
object AnalysisActions {

    /**
     * Запустить OnSave анализ
     */
    fun runOnSaveAnalysis(project: Project, filepathList: List<String>) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
            override fun run(indicator: ProgressIndicator) = with(inferProjectComponent) {
                if (pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                inferRunner.runFileAnalysis(pluginSettings.buildTool, filepathList, indicator)
                resultsTab.updateCurrentFileTree()
                resultsTab.updateFullReportTree()
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }

    /**
     * Запустить анализ на текущем файле, открытом в редакторе
     */
    fun runFileAnalysis(project: Project) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
            override fun run(indicator: ProgressIndicator) = with(inferProjectComponent) {
                if (pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                inferRunner.runFileAnalysis(pluginSettings.buildTool, listOf(virtualFile.canonicalPath!!), indicator)
                resultsTab.updateCurrentFileTree()
                resultsTab.updateFullReportTree()
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }

    /**
     * Запустить анализ на текущем модуле, который соответствует открытому в редакторе файлу
     */
    fun runModuleAnalysis(project: Project) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
            override fun run(indicator: ProgressIndicator) = with(inferProjectComponent) {
                if (pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                inferRunner.runModuleAnalysis(pluginSettings.buildTool, virtualFile.canonicalPath!!, indicator)
                resultsTab.updateCurrentFileTree()
                resultsTab.updateFullReportTree()
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }

    /**
     * Запустить анализ всех модулей
     */
    fun runAllModulesAnalysis(project: Project) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
            override fun run(indicator: ProgressIndicator) = with(inferProjectComponent) {
                if (pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                inferRunner.runAllModulesAnalysis(pluginSettings.buildTool, indicator)
                resultsTab.updateCurrentFileTree()
                resultsTab.updateFullReportTree()
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }

    /**
     * Запустить предварительный анализ
     */
    fun runPreAnalysis(project: Project) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running......") {
            override fun run(indicator: ProgressIndicator) = with(inferProjectComponent) {
                if (pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                inferRunner.runPreAnalysis(pluginSettings.buildTool, indicator)
                resultsTab.updateCurrentFileTree()
                resultsTab.updateFullReportTree()
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }
}
