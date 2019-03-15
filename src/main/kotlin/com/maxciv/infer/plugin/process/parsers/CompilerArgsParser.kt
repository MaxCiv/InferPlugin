package com.maxciv.infer.plugin.process.parsers

import com.maxciv.infer.plugin.process.BuildTools

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface CompilerArgsParser {

    fun getCompilerArgs(buildTool: BuildTools, projectPath: String): List<String>
}
