package com.maxciv.infer.plugin.actions.tabs.results

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.actionSystem.ToggleAction
import com.maxciv.infer.plugin.InferProjectComponent

/**
 * @author maxim.oleynik
 * @since 29.04.2019
 */
class AutoscrollToSourceAction : ToggleAction() {

    override fun isSelected(event: AnActionEvent): Boolean {
        val project = event.getData(DataKeys.PROJECT) ?: return false
        val pluginSettings = project.getComponent(InferProjectComponent::class.java).pluginSettings
        return pluginSettings.isAutoscrollToSourceEnabled
    }

    override fun setSelected(event: AnActionEvent, state: Boolean) {
        val project = event.getData(DataKeys.PROJECT) ?: return
        val pluginSettings = project.getComponent(InferProjectComponent::class.java).pluginSettings
        pluginSettings.isAutoscrollToSourceEnabled = state
    }
}
