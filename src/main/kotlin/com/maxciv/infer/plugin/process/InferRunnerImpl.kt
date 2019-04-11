package com.maxciv.infer.plugin.process

import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.data.report.ProjectModule
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParser
import com.maxciv.infer.plugin.process.parsers.ProjectModulesParserImpl
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

    private val javaFilesRegex = """\.java$""".toRegex()
    private val shell: ShellCommandExecutor = ShellCommandExecutorImpl(File(projectPath))
    private val projectModulesParser: ProjectModulesParser = ProjectModulesParserImpl()

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
        }
        pluginSettings.projectModules = projectModulesParser.getProjectModules(buildTool, projectPath).toMutableList()
        return ReportProducer.produceInferReport(projectPath)
    }

    override fun runAnalysis(buildTool: BuildTools, filename: String): InferReport {
        // Проверка на соответствие файлу.java
        if (!filename.contains(javaFilesRegex)) return InferReport()

        // Ищем соответствующий файлу модуль, если не находим – возвращаем пустой отчет
        val currentModule = getModuleForFile(filename, pluginSettings.projectModules)
        if (currentModule.compilerArgs.isEmpty()) return InferReport()

        when (buildTool) {
            BuildTools.MAVEN -> {
                javac(filename, currentModule.compilerArgs)
            }
            BuildTools.GRADLEW, BuildTools.GRADLE -> {
                javac(filename, currentModule.compilerArgs)
            }
        }

        val changedFilesIndex = createChangedFilesIndex(filename)
        analyze(changedFilesIndex)
        return ReportProducer.produceInferReport(projectPath)
    }

    /**
     * Вернуть модуль, к которому относится файл
     */
    private fun getModuleForFile(filename: String, projectModules: List<ProjectModule>): ProjectModule {
        // Если модуль только один, то берём сразу его
        if (projectModules.count() == 1) return projectModules[0]

        // Если модулей несколько, ищем файл во всех модулях
        val requiredModule = projectModules.asSequence()
            .filter { it.sourceFiles.contains(filename) }
            .toList()

        // Если нашли файл по точному совпадению в одном из модулей, возвращаем его
        if (requiredModule.isNotEmpty()) return requiredModule[0]

        // Если файл новый, проверим какому -sourcepath какого модуля он соответствует и вернём его
        val possibleModule = projectModules.asSequence()
            .filter {
                it.compilerArgs.asSequence()
                    .dropWhile { !it.contains("-sourcepath") }
                    .drop(1)
                    .first()
                    .split(File.pathSeparator)
                    .any { filename.contains(it) }
            }
            .toList()

        // Если подходящего модуля не нашлось, возвращаем пустой
        return if (possibleModule.isNotEmpty()) possibleModule[0] else ProjectModule(listOf(), listOf())
    }

    private fun javac(filename: String, compilerArgs: List<String>): CommandResult {
        return shell.execute(
            listOf(pluginSettings.inferPath, "--reactive", "capture", "--", "javac", filename)
                .plus(compilerArgs)
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
