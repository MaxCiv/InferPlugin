package com.maxciv.infer.plugin.process.parsers

import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.process.BuildTools
import com.maxciv.infer.plugin.process.BuildTools.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.Reader

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
class ProjectModulesParserImpl : ProjectModulesParser {

    override fun getProjectModules(buildTool: BuildTools, inferWorkingDir: String): List<ProjectModule> {
        val logsFile = File("$inferWorkingDir/logs")
        if (!logsFile.exists()) return listOf()

        val logLines = BufferedReader(FileReader(logsFile) as Reader?).readText()
        return when (buildTool) {
            MAVEN, MAVENW -> MavenParser.getCompilerArgs(logLines)
            GRADLE, GRADLEW -> GradleParser.getCompilerArgs(logLines)
            else -> listOf()
        }
    }
}
