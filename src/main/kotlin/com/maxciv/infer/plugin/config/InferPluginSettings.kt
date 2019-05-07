package com.maxciv.infer.plugin.config

import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.BuildTools
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
data class InferPluginSettings(
    var inferPath: String = "infer",
    var buildTool: BuildTools = BuildTools.DEFAULT,
    var projectModules: MutableList<ProjectModule> = mutableListOf(),
    var isAutoscrollToSourceEnabled: Boolean = true,
    var isOnSaveAnalyzeEnabled: Boolean = true,
    var aggregatedInferReport: InferReport = InferReport(),

    @Transient val analysisCounter: AtomicInteger = AtomicInteger(0)
)
