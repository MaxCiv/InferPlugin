package com.maxciv.infer.plugin.process.onsave

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.actions.AnalysisActions

/**
 * Проводит анализ файла после его сохранения
 *
 * @author maxim.oleynik
 * @since 29.04.2019
 */
class OnSaveAnalyzeListener(
    private val project: Project,
    private val inferProjectComponent: InferProjectComponent = project.getComponent(InferProjectComponent::class.java)
) : VirtualFileListener {

    override fun contentsChanged(event: VirtualFileEvent) {
        if (inferProjectComponent.pluginSettings.isOnSaveAnalyzeEnabled
            && event.isFromSave
            && event.fileName.endsWith(".java")
        ) {
            analyseFile(event)
        }
    }

    private fun analyseFile(event: VirtualFileEvent) {
        val selectedFiles = FileEditorManager.getInstance(project).selectedFiles
        if (selectedFiles.contains(event.file)) AnalysisActions.runOnSaveAnalysis(project, event.file)
    }
}
