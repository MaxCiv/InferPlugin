package com.maxciv.infer.plugin.process.shell

import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.process.ProjectModuleUtils.inferResultsDir
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
                "--results-dir", inferResultsDir(pluginSettings, projectModule),
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
                "--results-dir", inferResultsDir(pluginSettings, projectModule),
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
                "--results-dir", inferResultsDir(pluginSettings, projectModule),
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

    //region mavenW
    fun mavenwClean(): CommandResult =
        shellCommandExecutor.execute(listOf("./mvnw", "clean", "--quiet"))

    fun mavenwCompile(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(listOf("./mvnw", mavenCaptureTask, "--quiet").plus(mavenUserArguments))
    }

    fun mavenwCompileModule(moduleName: String): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf("./mvnw", mavenCaptureTask, "--quiet", "-pl", moduleName, "--quiet").plus(mavenUserArguments)
        )
    }

    fun mavenwCapture(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./mvnw", mavenCaptureTask, "--quiet"
            ).plus(mavenUserArguments)
        )
    }
    //endregion

    //region maven
    fun mavenClean(): CommandResult =
        shellCommandExecutor.execute(listOf("mvn", "clean", "--quiet"))

    fun mavenCompile(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(listOf("mvn", mavenCaptureTask, "--quiet").plus(mavenUserArguments))
    }

    fun mavenCompileModule(moduleName: String): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                "mvn",
                mavenCaptureTask,
                "--quiet",
                "-pl",
                moduleName
            ).plus(mavenUserArguments)
        )
    }

    fun mavenCapture(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "mvn", mavenCaptureTask, "--quiet"
            ).plus(mavenUserArguments)
        )
    }
    //endregion

    //region gradleW
    fun gradlewClean(): CommandResult =
        shellCommandExecutor.execute(listOf("./gradlew", "clean"))

    fun gradlewCompile(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                "./gradlew",
                gradleCaptureTask,
                "-x",
                "test"
            ).plus(pluginSettings.gradleUserArguments)
        )
    }

    fun gradlewCompileModule(moduleName: String): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf("./gradlew", ":$moduleName:$gradleCaptureTask", "-x", "test").plus(
                pluginSettings.gradleUserArguments
            )
        )
    }

    fun gradlewCapture(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./gradlew", gradleCaptureTask, "-x", "test"
            ).plus(gradleUserArguments)
        )
    }
    //endregion

    //region gradle
    fun gradleClean(): CommandResult =
        shellCommandExecutor.execute(listOf("gradle", "clean"))

    fun gradleCompile(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                "gradle",
                gradleCaptureTask,
                "-x",
                "test"
            ).plus(pluginSettings.gradleUserArguments)
        )
    }

    fun gradleCompileModule(moduleName: String): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                "gradle",
                ":$moduleName:$gradleCaptureTask",
                "-x",
                "test"
            ).plus(pluginSettings.gradleUserArguments)
        )
    }

    fun gradleCapture(): CommandResult = with(pluginSettings) {
        shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "gradle", gradleCaptureTask, "-x", "test"
            ).plus(gradleUserArguments)
        )
    }
    //endregion
}
