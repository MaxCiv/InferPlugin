package com.maxciv.infer.plugin

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.config.InferPluginState
import com.maxciv.infer.plugin.process.InferRunner
import com.maxciv.infer.plugin.process.InferRunnerImpl
import com.maxciv.infer.plugin.process.onsave.OnSaveService
import com.maxciv.infer.plugin.ui.toolwindow.ResultsTab
import com.maxciv.infer.plugin.ui.toolwindow.SettingsTab

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
class InferProjectComponent(val project: Project) : ProjectComponent {
    val pluginSettings: InferPluginSettings = ServiceManager.getService(project, InferPluginState::class.java).state!!
    val inferRunner: InferRunner = InferRunnerImpl(project, pluginSettings)

    lateinit var resultsTab: ResultsTab
    lateinit var settingsTab: SettingsTab

    override fun projectOpened() {
        OnSaveService.getInstance(project).registerOnSaveListener()
    }

    override fun projectClosed() {
        OnSaveService.getInstance(project).unregisterOnSaveListener()
    }

    companion object {
        const val ID_PLUGIN = "InferPlugin"
    }
}
