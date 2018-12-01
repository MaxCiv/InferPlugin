package com.maxciv.infer.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.maxciv.infer.plugin.InferProjectComponent;
import com.maxciv.infer.plugin.process.DefineBuildTool;

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
@State(name = InferProjectComponent.ID_PLUGIN, storages = {@Storage("infer-plugin.xml")})
public class InferPluginState implements PersistentStateComponent<InferPluginSettings> {

    private InferPluginSettings pluginSettings;

    public InferPluginState(Project project) {
        this.pluginSettings = defaultPluginSettings(project);
    }

    @Override
    public InferPluginSettings getState() {
        return pluginSettings;
    }

    @Override
    public void loadState(InferPluginSettings state) {
        if (state != null) {
            pluginSettings = state;
        }
    }

    private InferPluginSettings defaultPluginSettings(Project project) {
        InferPluginSettings defaultSettings = new InferPluginSettings();
        defaultSettings.setBuildTool(DefineBuildTool.defineFor(project));
        return defaultSettings;
    }
}
