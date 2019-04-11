package com.maxciv.infer.plugin.config

import com.maxciv.infer.plugin.data.report.ProjectModule
import com.maxciv.infer.plugin.process.BuildTools

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
class InferPluginSettings {

    var inferPath = "infer"
    var buildTool = BuildTools.DEFAULT
    var projectModules = mutableListOf<ProjectModule>()
}
