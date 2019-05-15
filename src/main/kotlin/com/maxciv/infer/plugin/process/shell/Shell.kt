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
    fun javac(filenames: List<String>, projectModule: ProjectModule): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", getInferWorkingDirForModule(pluginSettings.inferWorkingDir, projectModule),
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

    fun analyze(changedFilesIndex: File, projectModule: ProjectModule): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", getInferWorkingDirForModule(pluginSettings.inferWorkingDir, projectModule),
                "--no-progress-bar",
                "analyze",
                "--changed-files-index", changedFilesIndex.canonicalPath
            )
        )
    }

    fun analyzeClassFiles(projectModule: ProjectModule): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", getInferWorkingDirForModule(pluginSettings.inferWorkingDir, projectModule),
                "--no-progress-bar",
                "--classpath", projectModule.getClasspath(),
                "--sourcepath", projectModule.getSourcePath().split(":").first().trim(),
                "--generated-classes", projectModule.getGeneratedClasses()
            )
        )
    }

    fun analyzeAll(): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", pluginSettings.inferWorkingDir,
                "--no-progress-bar",
                "analyze"
            )
        )
    }

    fun mavenClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("mvn", "clean"))
    }

    fun mavenCompile(): CommandResult {
        return shellCommandExecutor.execute(listOf("mvn", "compile"))
    }

    fun mavenCompileModule(moduleName: String): CommandResult {
        return shellCommandExecutor.execute(listOf("mvn", "compile", "-pl", moduleName))
    }

    fun mavenCapture(): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", pluginSettings.inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "mvn", "compile"
            )
        )
    }

    fun gradlewClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("./gradlew", "clean"))
    }

    fun gradlewCompile(): CommandResult {
        return shellCommandExecutor.execute(listOf("./gradlew", "build"))
    }

    fun gradlewCompileModule(moduleName: String): CommandResult {
        return shellCommandExecutor.execute(listOf("./gradlew", ":$moduleName:build"))
    }

    fun gradlewCapture(): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", pluginSettings.inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./gradlew", "build"
            )
        )
    }

    fun gradleClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("gradle", "clean"))
    }

    fun gradleCompile(): CommandResult {
        return shellCommandExecutor.execute(listOf("gradle", "build"))
    }

    fun gradleCompileModule(moduleName: String): CommandResult {
        return shellCommandExecutor.execute(listOf("gradle", ":$moduleName:build"))
    }

    fun gradleCapture(): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                pluginSettings.inferPath,
                "--results-dir", pluginSettings.inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "gradle", "build"
            )
        )
    }
}
