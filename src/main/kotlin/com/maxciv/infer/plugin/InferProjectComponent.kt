package com.maxciv.infer.plugin

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.config.InferPluginState
import com.maxciv.infer.plugin.toolwindow.ResultsTab
import com.maxciv.infer.plugin.toolwindow.SettingsTab

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
class InferProjectComponent(project: Project) : ProjectComponent {
    val pluginSettings: InferPluginSettings? = ServiceManager.getService(project, InferPluginState::class.java).state

    var resultsTab: ResultsTab? = null
    var settingsTab: SettingsTab? = null

    companion object {
        const val ID_PLUGIN = "InferPlugin"
    }
}
