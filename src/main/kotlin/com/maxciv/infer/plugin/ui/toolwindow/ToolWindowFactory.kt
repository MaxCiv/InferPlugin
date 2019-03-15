package com.maxciv.infer.plugin.ui.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowType

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
class ToolWindowFactory : com.intellij.openapi.wm.ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val results = toolWindow.contentManager.factory.createContent(
                ResultsTab(project),
                "Results",
                false)
        toolWindow.contentManager.addContent(results)

        val settings = toolWindow.contentManager.factory.createContent(
                SettingsTab(project),
                "Settings",
                false)
        toolWindow.contentManager.addContent(settings)

        toolWindow.setType(ToolWindowType.DOCKED, null)
    }
}
