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
     * Запустить анализ на текущем файле, открытом в редакторе
     */
    fun runFileAnalysis(project: Project) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
            override fun run(indicator: ProgressIndicator) {
                if (inferProjectComponent.pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                project.getComponent(InferProjectComponent::class.java).resultsTab.fillTreeFromResult(
                    inferProjectComponent.inferRunner.runFileAnalysis(
                        inferProjectComponent.pluginSettings.buildTool,
                        virtualFile
                    )
                )
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
            override fun run(indicator: ProgressIndicator) {
                if (inferProjectComponent.pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                project.getComponent(InferProjectComponent::class.java).resultsTab.fillTreeFromResult(
                    inferProjectComponent.inferRunner.runModuleAnalysis(
                        inferProjectComponent.pluginSettings.buildTool,
                        virtualFile
                    )
                )
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }

    /**
     * Запустить анализ на всём проекте
     */
    fun runProjectAnalysis(project: Project) {
        val inferProjectComponent = project.getComponent(InferProjectComponent::class.java)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running......") {
            override fun run(indicator: ProgressIndicator) {
                if (inferProjectComponent.pluginSettings.analysisCounter.getAndIncrement() != 0) return
                indicator.isIndeterminate = true
                inferProjectComponent.resultsTab.fillTreeFromResult(
                    inferProjectComponent.inferRunner.runProjectAnalysis(inferProjectComponent.pluginSettings.buildTool)
                )
                inferProjectComponent.settingsTab.compilerArgsTextField.text =
                    inferProjectComponent.pluginSettings.projectModules.joinToString(" ")
            }

            override fun onFinished() {
                inferProjectComponent.pluginSettings.analysisCounter.getAndDecrement()
            }
        })
    }
}
