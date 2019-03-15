package com.maxciv.infer.plugin.process

import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.parsers.CompilerArgsParser
import com.maxciv.infer.plugin.process.parsers.CompilerArgsParserImpl
import com.maxciv.infer.plugin.process.shell.CommandResult
import com.maxciv.infer.plugin.process.shell.ShellCommandExecutor
import com.maxciv.infer.plugin.process.shell.ShellCommandExecutorImpl
import java.io.File

/**
 * @author maxim.oleynik
 * @since 30.11.2018
 */
class InferRunnerImpl(
    private val projectPath: String,
    private val pluginSettings: InferPluginSettings
) : InferRunner {

    private val shell: ShellCommandExecutor = ShellCommandExecutorImpl(File(projectPath))
    private val compilerArgsParser: CompilerArgsParser = CompilerArgsParserImpl()

    override fun runAnalysis(filename: String): InferReport {
        javac(filename)
        val changedFilesIndex = createChangedFilesIndex(filename)
        analyze(changedFilesIndex)
        return ReportProducer.produceInferReport(projectPath)
    }

    override fun runFullAnalysis(buildTool: BuildTools): InferReport {
        when (buildTool) {
            BuildTools.MAVEN -> {
                mavenClean()
                mavenRun()
            }
            BuildTools.GRADLEW -> {
                gradlewClean()
                gradlewRun()
            }
            BuildTools.GRADLE -> {
                gradleClean()
                gradleRun()
            }
            else -> {
            }
        }
        pluginSettings.compilerArgs = compilerArgsParser.getCompilerArgs(buildTool, projectPath).toMutableList()
        return ReportProducer.produceInferReport(projectPath)
    }

    private fun javac(filename: String): CommandResult {
        return shell.execute(
            listOf(pluginSettings.inferPath, "--reactive", "capture", "--", "javac", filename)
                .plus(pluginSettings.compilerArgs)
        )
    }

    private fun createChangedFilesIndex(filename: String): File {
        val changedFilesIndex = createTempFile("infer-changed-files-index", ".index")
        changedFilesIndex.writeText(filename)
        changedFilesIndex.deleteOnExit()
        return changedFilesIndex
    }

    private fun analyze(changedFilesIndex: File): CommandResult {
        return shell.execute(
            listOf(pluginSettings.inferPath, "analyze", "--changed-files-index", changedFilesIndex.canonicalPath)
        )
    }

    private fun mavenClean(): CommandResult {
        return shell.execute(listOf("mvn", "clean"))
    }

    private fun mavenRun(): CommandResult {
        return shell.execute(listOf(pluginSettings.inferPath, "run", "--", "mvn", "compile"))
    }

    private fun gradlewClean(): CommandResult {
        return shell.execute(listOf("./gradlew", "clean"))
    }

    private fun gradlewRun(): CommandResult {
        return shell.execute(listOf(pluginSettings.inferPath, "run", "--", "./gradlew", "-d", "build"))
    }

    private fun gradleClean(): CommandResult {
        return shell.execute(listOf("gradle", "clean"))
    }

    private fun gradleRun(): CommandResult {
        return shell.execute(listOf(pluginSettings.inferPath, "run", "--", "gradle", "-d", "build"))
    }
}
