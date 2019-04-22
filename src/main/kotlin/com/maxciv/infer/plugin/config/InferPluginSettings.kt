package com.maxciv.infer.plugin.config

import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.process.BuildTools

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
data class InferPluginSettings(
    var inferPath: String = "infer",
    var buildTool: BuildTools = BuildTools.DEFAULT,
    var projectModules: MutableList<ProjectModule> = mutableListOf()
)
