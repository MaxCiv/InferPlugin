package com.maxciv.infer.plugin.process

import com.intellij.openapi.progress.ProgressIndicator
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParser
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParserImpl
import com.maxciv.infer.plugin.process.shell.Shell
import com.maxciv.infer.plugin.process.shell.ShellCommandExecutorImpl
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

    override fun runAllModulesAnalysis(buildTool: BuildTools, indicator: ProgressIndicator?): InferReport {
        if (pluginSettings.isCompileOnModuleAnalysisEnabled) {
            indicator.updateText("Infer: Compiling...")
            when (buildTool) {
                BuildTools.MAVEN -> shell.mavenCompile()
                BuildTools.GRADLEW -> shell.gradlewCompile()
                BuildTools.GRADLE -> shell.gradleCompile()
                else -> return InferReport()
            }
        }

        pluginSettings.projectModules.forEachIndexed { index, module ->
            indicator.updateText(
                "Infer: Analysing ${index + 1}...",
                (index + 1) / pluginSettings.projectModules.count().toDouble()
            )
            shell.analyzeClassFiles(module)
            val inferReport = ReportProducer.produceInferReport(projectPath)
            pluginSettings.aggregatedInferReport.updateForModuleReport(inferReport, module, projectPath)
        }
        return pluginSettings.aggregatedInferReport
    }

    override fun runModuleAnalysis(
        buildTool: BuildTools,
        filepath: String,
        indicator: ProgressIndicator?
    ): InferReport {
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

    override fun runFileAnalysis(
        buildTool: BuildTools,
        filepathList: List<String>,
        indicator: ProgressIndicator?
    ): InferReport {
        val javaFiles = filepathList.filter { it.endsWith(".java") }
        if (javaFiles.isEmpty()) return InferReport()

        val moduleToFilesMap =
            pluginSettings.projectModules.associateBy({ it }, { mutableListOf<String>() }).toMutableMap()
        moduleToFilesMap[ProjectModule(listOf(), listOf())] = mutableListOf()

        filepathList.forEach { filepath ->
            val moduleOfFile = ProjectModuleUtils.getModuleForFile(filepath, pluginSettings.projectModules)
            moduleToFilesMap[moduleOfFile]!!.add(filepath)
        }

        moduleToFilesMap.filter { it.value.isNotEmpty() }.forEach { (currentModule, fileList) ->
            if (currentModule.compilerArgs.isEmpty()) return@forEach

            indicator.updateText("Infer: Capturing...")
            deleteRacerdResults()
            shell.javac(fileList, currentModule.compilerArgs)

            indicator.updateText("Infer: Analysing...")
            val changedFilesIndex = createChangedFilesIndex(fileList)
            shell.analyze(changedFilesIndex)

            val inferReport = ReportProducer.produceInferReport(projectPath)
            pluginSettings.aggregatedInferReport.updateForFiles(fileList, inferReport, projectPath)
        }
        return pluginSettings.aggregatedInferReport
    }

    private fun createChangedFilesIndex(filenames: List<String>): File {
        return createTempFile("infer-changed-files-index", ".index").apply {
            writeText(filenames.joinToString("\n"))
            deleteOnExit()
        }
    }

    private fun deleteRacerdResults() {
        val racerdDir = File(projectPath + File.separator + "infer-out", "racerd")
        if (racerdDir.exists()) racerdDir.deleteRecursively()
    }
}
