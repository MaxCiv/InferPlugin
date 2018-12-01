package com.maxciv.infer.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.maxciv.infer.plugin.config.InferPluginSettings;
import com.maxciv.infer.plugin.config.InferPluginState;
import com.maxciv.infer.plugin.toolwindow.ResultsTab;
import com.maxciv.infer.plugin.toolwindow.SettingsTab;

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
public class InferProjectComponent implements ProjectComponent {

    public static final String ID_PLUGIN = "InferPlugin";

    private final Project project;
    private final InferPluginSettings pluginSettings;

    private ResultsTab resultsTab;
    private SettingsTab settingsTab;

    public InferProjectComponent(Project project) {
        this.project = project;
        this.pluginSettings = ServiceManager.getService(project, InferPluginState.class).getState();
    }

    public Project getProject() {
        return project;
    }

    public InferPluginSettings getPluginSettings() {
        return pluginSettings;
    }

    public ResultsTab getResultsTab() {
        return resultsTab;
    }

    public void setResultsTab(ResultsTab resultsTab) {
        this.resultsTab = resultsTab;
    }

    public SettingsTab getSettingsTab() {
        return settingsTab;
    }

    public void setSettingsTab(SettingsTab settingsTab) {
        this.settingsTab = settingsTab;
    }
}
