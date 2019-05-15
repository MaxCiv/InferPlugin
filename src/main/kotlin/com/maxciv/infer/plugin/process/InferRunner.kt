package com.maxciv.infer.plugin.process

import com.intellij.openapi.progress.ProgressIndicator
import com.maxciv.infer.plugin.data.report.InferReport

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface InferRunner {

    fun runPreAnalysis(buildTool: BuildTools, indicator: ProgressIndicator? = null): InferReport
    fun runAllModulesAnalysis(buildTool: BuildTools, indicator: ProgressIndicator? = null, shouldCompile: Boolean = true): InferReport
    fun runModuleAnalysis(buildTool: BuildTools, filepath: String, indicator: ProgressIndicator? = null): InferReport
    fun runFileAnalysis(buildTool: BuildTools, filepathList: List<String>, indicator: ProgressIndicator? = null): InferReport
}
