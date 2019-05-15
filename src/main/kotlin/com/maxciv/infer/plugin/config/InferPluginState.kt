package com.maxciv.infer.plugin.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.process.DefineBuildTool
import java.io.File

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
@State(name = InferProjectComponent.ID_PLUGIN, storages = [Storage("infer-plugin.xml")])
class InferPluginState(project: Project) : PersistentStateComponent<InferPluginSettings> {

    private var pluginSettings: InferPluginSettings

    init {
        this.pluginSettings = defaultPluginSettings(project)
    }

    override fun getState(): InferPluginSettings? {
        return pluginSettings
    }

    override fun loadState(state: InferPluginSettings) {
        pluginSettings = state
    }

    private fun defaultPluginSettings(project: Project): InferPluginSettings {
        val defaultSettings = InferPluginSettings()
        defaultSettings.buildTool = DefineBuildTool.defineFor(project)
        defaultSettings.inferWorkingDir = project.basePath!! + File.separator + ".idea" + File.separator + "infer-out"
        return defaultSettings
    }
}
