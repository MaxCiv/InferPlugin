package com.maxciv.infer.plugin.process.parsers

import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.process.BuildTools

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface ProjectModulesParser {

    fun getProjectModules(buildTool: BuildTools, projectPath: String): List<ProjectModule>
}
