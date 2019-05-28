package com.maxciv.infer.plugin.process

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.process.ProjectModuleUtils.getIdeaModuleForFile
import com.maxciv.infer.plugin.process.ProjectModuleUtils.inferResultsDir
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParser
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParserImpl
import com.maxciv.infer.plugin.process.report.ReportProducer
import com.maxciv.infer.plugin.process.shell.Shell
import com.maxciv.infer.plugin.process.shell.nuproc.ShellCommandExecutorNuImpl
import com.maxciv.infer.plugin.realName
import com.maxciv.infer.plugin.updateText
import java.io.File

/**
 * @author maxim.oleynik
 * @since 30.11.2018
 */
class InferRunnerImpl(
    private val project: Project,
    private val pluginSettings: InferPluginSettings,
    private val projectPath: String = project.basePath!!
) : InferRunner {

    private val shell: Shell = Shell(
        ShellCommandExecutorNuImpl(File(projectPath)),
        pluginSettings
    )
    private val projectModulesParser: ProjectModulesParser = ProjectModulesParserImpl()

    //FIXME 50% переделать запуск процессов, их остановку, вывод

    //FIXME анализ зависает, если в классе ошибка компиляции
    //FIXME нормальное скачивание Инфера, прокси
    //FIXME удалять ошибки для несуществующих файлов
    //FIXME использовать .inferconfig
    //FIXME возможно class-анализ не может запускаться на чистую

    override fun runPreAnalysis(buildTool: BuildTools, indicator: ProgressIndicator?): InferReport =
        with(pluginSettings) {
            indicator.updateText("Infer: Cleaning...")
            when (buildTool) {
                BuildTools.MAVENW -> {
                    shell.mavenwClean()
                    indicator.updateText("Infer: Capturing MavenW-Compile...")
                    shell.mavenwCapture()
                }
                BuildTools.MAVEN -> {
                    shell.mavenClean()
                    indicator.updateText("Infer: Capturing Maven-Compile...")
                    shell.mavenCapture()
                }
                BuildTools.GRADLEW -> {
                    shell.gradlewClean()
                    indicator.updateText("Infer: Capturing GradleW...")
                    shell.gradlewCapture()
                }
                BuildTools.GRADLE -> {
                    shell.gradleClean()
                    indicator.updateText("Infer: Capturing Gradle...")
                    shell.gradleCapture()
                }
                else -> return InferReport()
            }
            projectModules = projectModulesParser.getProjectModules(buildTool, inferWorkingDir).toMutableList()

            indicator.updateText("Infer: Analysing...")
            shell.analyzeAll()

            indicator.updateText("Infer: Finishing...")
            val inferReport = ReportProducer.produceInferReport(projectPath, inferWorkingDir)
            aggregatedInferReport = inferReport
            return aggregatedInferReport
//        return runAllModulesAnalysis(buildTool, indicator, shouldCompile = false)
        }

    override fun runAllModulesAnalysis(
        buildTool: BuildTools,
        indicator: ProgressIndicator?,
        shouldCompile: Boolean
    ): InferReport = with(pluginSettings) {
        if (isCompileOnModuleAnalysisEnabled && shouldCompile) {
            indicator.updateText("Infer: Compiling...")
            when (buildTool) {
                BuildTools.MAVENW -> shell.mavenwCompile()
                BuildTools.MAVEN -> shell.mavenCompile()
                BuildTools.GRADLEW -> shell.gradlewCompile()
                BuildTools.GRADLE -> shell.gradleCompile()
                else -> return InferReport()
            }
        }

        projectModules.forEachIndexed { index, module ->
            val moduleName = getIdeaModuleForFile(module.sourceFiles.getOrElse(0) { "some" }, project)?.name ?: ""
            indicator.updateText(
                "Infer: Analysing $moduleName (${index + 1})...",
                (index + 1) / projectModules.count().toDouble()
            )
            shell.analyzeClassFiles(module)
            val inferReport = ReportProducer.produceInferReport(
                projectPath, inferResultsDir(pluginSettings, module)
            )
            aggregatedInferReport.updateForModuleReport(inferReport, module, projectPath)
        }
        return aggregatedInferReport
    }

    override fun runModuleAnalysis(
        buildTool: BuildTools,
        filepath: String,
        indicator: ProgressIndicator?
    ): InferReport = with(pluginSettings) {
        val currentModule = ProjectModuleUtils.getModuleForFile(filepath, projectModules)
        if (currentModule.compilerArgs.isEmpty()) return InferReport()

        if (isCompileOnModuleAnalysisEnabled) {
            indicator.updateText("Infer: Compiling...")
            val module = getIdeaModuleForFile(filepath, project)
            if (module != null && isCompileOnlyOneModuleOnModuleAnalysisEnabled) {
                val moduleName = module.realName()
                when (buildTool) {
                    BuildTools.MAVENW -> shell.mavenwCompileModule(moduleName)
                    BuildTools.MAVEN -> shell.mavenCompileModule(moduleName)
                    BuildTools.GRADLEW -> shell.gradlewCompileModule(moduleName)
                    BuildTools.GRADLE -> shell.gradleCompileModule(moduleName)
                    else -> return InferReport()
                }
            } else {
                when (buildTool) {
                    BuildTools.MAVENW -> shell.mavenwCompile()
                    BuildTools.MAVEN -> shell.mavenCompile()
                    BuildTools.GRADLEW -> shell.gradlewCompile()
                    BuildTools.GRADLE -> shell.gradleCompile()
                    else -> return InferReport()
                }
            }
        }

        indicator.updateText("Infer: Analysing...")
        shell.analyzeClassFiles(currentModule)
        indicator.updateText("Infer: Finishing...")
        val inferReport = ReportProducer.produceInferReport(
            projectPath,
            inferResultsDir(pluginSettings, currentModule)
        )
        aggregatedInferReport.updateForModuleReport(inferReport, currentModule, projectPath)
        return inferReport
    }

    override fun runFileAnalysis(
        buildTool: BuildTools,
        filepathList: List<String>,
        indicator: ProgressIndicator?
    ): InferReport = with(pluginSettings) {
        val javaFiles = filepathList.filter { it.endsWith(".java") }
        if (javaFiles.isEmpty()) return InferReport()

        val moduleToFilesMap = projectModules.associateBy({ it }, { mutableListOf<String>() }).toMutableMap()
        moduleToFilesMap[ProjectModule(listOf(), listOf())] = mutableListOf()

        filepathList.forEach { filepath ->
            val moduleOfFile = ProjectModuleUtils.getModuleForFile(filepath, projectModules)
            moduleToFilesMap[moduleOfFile]!!.add(filepath)
        }

        moduleToFilesMap.filter { it.value.isNotEmpty() }.forEach { (currentModule, fileList) ->
            if (currentModule.compilerArgs.isEmpty()) return@forEach

            indicator.updateText("Infer: Capturing...")
            deleteRacerdResults(currentModule)
            shell.javac(fileList, currentModule)

            indicator.updateText("Infer: Analysing...")
            val changedFilesIndex = createChangedFilesIndex(fileList)
            shell.analyze(changedFilesIndex, currentModule)

            val inferReport = ReportProducer.produceInferReport(
                projectPath,
                inferResultsDir(pluginSettings, currentModule)
            )
            aggregatedInferReport.updateForFiles(fileList, inferReport, projectPath)
        }
        return aggregatedInferReport
    }

    private fun createChangedFilesIndex(filenames: List<String>): File =
        createTempFile("infer-changed-files-index", ".index").apply {
            writeText(filenames.joinToString("\n"))
            deleteOnExit()
        }

    private fun deleteRacerdResults(module: ProjectModule) {
        val racerdDir = File(inferResultsDir(pluginSettings, module), "racerd")
        if (racerdDir.exists()) racerdDir.deleteRecursively()
    }
}
