package com.maxciv.infer.plugin.process

import com.maxciv.infer.plugin.data.report.InferReport

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface InferRunner {

    fun runFullAnalysis(buildTool: BuildTools): InferReport
    fun runAnalysis(buildTool: BuildTools, filename: String): InferReport
}
