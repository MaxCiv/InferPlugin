package com.maxciv.infer.plugin.process

import com.intellij.openapi.vfs.VirtualFile
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParser
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParserImpl
import com.maxciv.infer.plugin.process.shell.Shell
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

    private val shell: Shell = Shell(
        ShellCommandExecutorImpl(File(projectPath)),
        pluginSettings.inferPath
    )
    private val projectModulesParser: ProjectModulesParser = ProjectModulesParserImpl()

    override fun runProjectAnalysis(buildTool: BuildTools): InferReport {
        when (buildTool) {
            BuildTools.MAVEN -> {
                shell.mavenClean()
                shell.mavenCapture()
            }
            BuildTools.GRADLEW -> {
                shell.gradlewClean()
                shell.gradlewCapture()
            }
            BuildTools.GRADLE -> {
                shell.gradleClean()
                shell.gradleCapture()
            }
            else -> return InferReport()
        }
        shell.analyzeAll()
        pluginSettings.projectModules = projectModulesParser.getProjectModules(buildTool, projectPath).toMutableList()
        val inferReport = ReportProducer.produceInferReport(projectPath)
        pluginSettings.aggregatedInferReport = inferReport
        return inferReport
    }

    override fun runModuleAnalysis(buildTool: BuildTools, file: VirtualFile): InferReport {
        val filepath = file.canonicalPath!!
        val currentModule = ProjectModuleUtils.getModuleForFile(filepath, pluginSettings.projectModules)
        if (currentModule.compilerArgs.isEmpty()) return InferReport()

        shell.analyzeClassFiles(currentModule)
        val inferReport = ReportProducer.produceInferReport(projectPath)
        pluginSettings.aggregatedInferReport = inferReport
        return inferReport
    }

    override fun runFileAnalysis(buildTool: BuildTools, file: VirtualFile): InferReport {
        if (!file.extension.equals("java")) return InferReport()

        val filepath = file.canonicalPath!!
        val currentModule = ProjectModuleUtils.getModuleForFile(filepath, pluginSettings.projectModules)
        if (currentModule.compilerArgs.isEmpty()) return InferReport()

        deleteRacerdResults()
        shell.javac(filepath, currentModule.compilerArgs)

        val changedFilesIndex = createChangedFilesIndex(filepath)
        shell.analyze(changedFilesIndex)

        val inferReport = ReportProducer.produceInferReport(projectPath)
        val filename = file.canonicalPath!!.replace(projectPath + File.separator, "")
        pluginSettings.aggregatedInferReport.updateForFile(
            filename,
            inferReport.violationsByFile.getOrDefault(filename, listOf())
        )
        return inferReport
    }

    private fun createChangedFilesIndex(filename: String): File {
        return createTempFile("infer-changed-files-index", ".index").apply {
            writeText(filename)
            deleteOnExit()
        }
    }

    private fun deleteRacerdResults() {
        val racerdDir = File(projectPath + File.separator + "infer-out", "racerd")
        if (racerdDir.exists()) racerdDir.deleteRecursively()
    }
}
