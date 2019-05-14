package com.maxciv.infer.plugin.process

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.vfs.VirtualFile
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParser
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParserImpl
import com.maxciv.infer.plugin.process.shell.Shell
import com.maxciv.infer.plugin.process.shell.ShellCommandExecutorImpl
import com.maxciv.infer.plugin.toProjectRelativePath
import com.maxciv.infer.plugin.updateText
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

    override fun runProjectAnalysis(buildTool: BuildTools, indicator: ProgressIndicator?): InferReport {
        indicator.updateText("Infer: Cleaning...")
        when (buildTool) {
            BuildTools.MAVEN -> {
                shell.mavenClean()
                indicator.updateText("Infer: Capturing...")
                shell.mavenCapture()
            }
            BuildTools.GRADLEW -> {
                shell.gradlewClean()
                indicator.updateText("Infer: Capturing...")
                shell.gradlewCapture()
            }
            BuildTools.GRADLE -> {
                shell.gradleClean()
                indicator.updateText("Infer: Capturing...")
                shell.gradleCapture()
            }
            else -> return InferReport()
        }
        indicator.updateText("Infer: Analysing...")
        shell.analyzeAll()
        indicator.updateText("Infer: Finishing...")
        pluginSettings.projectModules = projectModulesParser.getProjectModules(buildTool, projectPath).toMutableList()
        val inferReport = ReportProducer.produceInferReport(projectPath)
        pluginSettings.aggregatedInferReport = inferReport
        return inferReport
    }

    override fun runModuleAnalysis(buildTool: BuildTools, file: VirtualFile, indicator: ProgressIndicator?): InferReport {
        val filepath = file.canonicalPath!!
        val currentModule = ProjectModuleUtils.getModuleForFile(filepath, pluginSettings.projectModules)
        if (currentModule.compilerArgs.isEmpty()) return InferReport()

        if (pluginSettings.isCompileOnModuleAnalysisEnabled) {
            indicator.updateText("Infer: Compiling...")
            when (buildTool) {
                BuildTools.MAVEN -> shell.mavenCompile()
                BuildTools.GRADLEW -> shell.gradlewCompile()
                BuildTools.GRADLE -> shell.gradleCompile()
                else -> return InferReport()
            }
        }

        indicator.updateText("Infer: Analysing...")
        shell.analyzeClassFiles(currentModule)
        indicator.updateText("Infer: Finishing...")
        val inferReport = ReportProducer.produceInferReport(projectPath)
        pluginSettings.aggregatedInferReport.updateForModuleReport(inferReport, currentModule, projectPath)
        return inferReport
    }

    override fun runFileAnalysis(buildTool: BuildTools, file: VirtualFile, indicator: ProgressIndicator?): InferReport {
        if (!file.extension.equals("java")) return InferReport()

        val filepath = file.canonicalPath!!
        val currentModule = ProjectModuleUtils.getModuleForFile(filepath, pluginSettings.projectModules)
        if (currentModule.compilerArgs.isEmpty()) return InferReport()

        indicator.updateText("Infer: Capturing...")
        deleteRacerdResults()
        shell.javac(filepath, currentModule.compilerArgs)

        indicator.updateText("Infer: Analysing...")
        val changedFilesIndex = createChangedFilesIndex(filepath)
        shell.analyze(changedFilesIndex)

        indicator.updateText("Infer: Finishing...")
        val inferReport = ReportProducer.produceInferReport(projectPath)
        val filename = file.canonicalPath!!.toProjectRelativePath(projectPath)
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
