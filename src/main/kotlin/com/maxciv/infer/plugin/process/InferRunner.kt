package com.maxciv.infer.plugin.process

import com.maxciv.infer.plugin.data.report.InferReport

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface InferRunner {

    fun runAnalysis(buildTool: BuildTools, filename: String): InferReport
    fun runFullAnalysis(buildTool: BuildTools): InferReport
}
