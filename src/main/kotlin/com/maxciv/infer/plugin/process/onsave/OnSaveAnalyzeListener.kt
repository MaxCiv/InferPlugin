package com.maxciv.infer.plugin.process.onsave

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.actions.AnalysisActions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private val waitTime = 200L

    override fun contentsChanged(event: VirtualFileEvent) {
        if (inferProjectComponent.pluginSettings.isOnSaveAnalyzeEnabled
            && event.isFromSave
            && event.fileName.endsWith(".java")
        ) {
            val currentTimeMillis = System.currentTimeMillis()
            if (isTimeToRunAnalysis(currentTimeMillis)) {
                inferProjectComponent.pluginSettings.lastAnalysisTime = currentTimeMillis
                GlobalScope.launch {
                    delay(waitTime)
                    val files = inferProjectComponent.pluginSettings.filesToAnalyse.toList()
                    inferProjectComponent.pluginSettings.filesToAnalyse.removeAll(files)
                    AnalysisActions.runOnSaveAnalysis(project, files)
                }
            }
            inferProjectComponent.pluginSettings.filesToAnalyse.add(event.file.canonicalPath!!)
        }
    }

    private fun isTimeToRunAnalysis(currentTimeMillis: Long): Boolean =
        currentTimeMillis - inferProjectComponent.pluginSettings.lastAnalysisTime > waitTime
}
