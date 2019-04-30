package com.maxciv.infer.plugin.actions.tabs.results

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.util.ui.tree.TreeUtil
import com.maxciv.infer.plugin.InferProjectComponent

/**
 * @author maxim.oleynik
 * @since 29.04.2019
 */
class CollapseAllAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(DataKeys.PROJECT) ?: return
        val resultsTab = project.getComponent(InferProjectComponent::class.java).resultsTab
        TreeUtil.collapseAll(resultsTab.treeResults, 2)
    }
}
