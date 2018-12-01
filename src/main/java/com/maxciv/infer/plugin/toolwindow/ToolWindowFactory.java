package com.maxciv.infer.plugin.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Content results = toolWindow.getContentManager().getFactory().createContent(
                new ResultsTab(project),
                "Results",
                false);
        toolWindow.getContentManager().addContent(results);

        Content settings = toolWindow.getContentManager().getFactory().createContent(
                new SettingsTab(project),
                "Settings",
                false);
        toolWindow.getContentManager().addContent(settings);

        toolWindow.setType(ToolWindowType.DOCKED, null);
    }
}
