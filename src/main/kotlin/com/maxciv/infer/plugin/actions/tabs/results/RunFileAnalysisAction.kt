package com.maxciv.infer.plugin.actions.tabs.results

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.maxciv.infer.plugin.actions.AnalysisActions

/**
 * @author maxim.oleynik
 * @since 24.04.2019
 */
class RunFileAnalysisAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent?) {
        val project = event!!.getData(DataKeys.PROJECT) ?: return
        AnalysisActions.runFileAnalysis(project)
    }
}
