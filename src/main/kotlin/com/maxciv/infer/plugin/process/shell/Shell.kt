package com.maxciv.infer.plugin.process.shell

import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.process.ProjectModuleUtils.getInferWorkingDirForModule
import java.io.File

/**
 * Консольные команды
 *
 * @author maxim.oleynik
 * @since 22.04.2019
 */
class Shell(
    private val shellCommandExecutor: ShellCommandExecutor,
    private val pluginSettings: InferPluginSettings
) {
    fun javac(filenames: List<String>, projectModule: ProjectModule): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", getInferWorkingDirForModule(inferWorkingDir, projectModule),
                "--no-progress-bar",
                "--reactive",
                "capture",
                "--",
                "javac"
            )
                .plus(filenames)
                .plus(projectModule.compilerArgs)
        )
    }

    fun analyze(changedFilesIndex: File, projectModule: ProjectModule): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", getInferWorkingDirForModule(inferWorkingDir, projectModule),
                "--no-progress-bar",
                "analyze",
                "--changed-files-index", changedFilesIndex.canonicalPath
            )
        )
    }

    fun analyzeClassFiles(projectModule: ProjectModule): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", getInferWorkingDirForModule(inferWorkingDir, projectModule),
                "--no-progress-bar",
                "--classpath", projectModule.getClasspath(),
                "--sourcepath", projectModule.getSourcePath().split(":").first().trim(),
                "--generated-classes", projectModule.getGeneratedClasses()
            )
        )
    }

    fun analyzeAll(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "analyze"
            )
        )
    }

    fun mavenClean(): CommandResult = shellCommandExecutor.execute(listOf("mvn", "clean"))

    fun mavenCompile(): CommandResult = shellCommandExecutor.execute(listOf("mvn", "compile"))

    fun mavenCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(listOf("mvn", "compile", "-pl", moduleName))

    fun mavenCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "mvn", "compile"
            )
        )
    }

    fun gradlewClean(): CommandResult = shellCommandExecutor.execute(listOf("./gradlew", "clean"))

    fun gradlewCompile(): CommandResult = shellCommandExecutor.execute(listOf("./gradlew", "build"))

    fun gradlewCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(listOf("./gradlew", ":$moduleName:build"))

    fun gradlewCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./gradlew", "build"
            )
        )
    }

    fun gradleClean(): CommandResult = shellCommandExecutor.execute(listOf("gradle", "clean"))

    fun gradleCompile(): CommandResult = shellCommandExecutor.execute(listOf("gradle", "build"))

    fun gradleCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(listOf("gradle", ":$moduleName:build"))

    fun gradleCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "gradle", "build"
            )
        )
    }
}
