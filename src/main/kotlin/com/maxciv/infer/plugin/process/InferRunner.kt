package com.maxciv.infer.plugin.process

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.vfs.VirtualFile
import com.maxciv.infer.plugin.data.report.InferReport

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface InferRunner {

    fun runProjectAnalysis(buildTool: BuildTools, indicator: ProgressIndicator? = null): InferReport
    fun runModuleAnalysis(buildTool: BuildTools, file: VirtualFile, indicator: ProgressIndicator? = null): InferReport
    fun runFileAnalysis(buildTool: BuildTools, file: VirtualFile, indicator: ProgressIndicator? = null): InferReport
}
