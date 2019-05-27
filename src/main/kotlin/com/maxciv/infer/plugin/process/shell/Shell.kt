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

    fun mavenwCompile(): CommandResult =
        shellCommandExecutor.execute(listOf("./mvnw", "compile", "--quiet").plus(pluginSettings.mavenUserArguments))

    fun mavenwCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(
            listOf("./mvnw", "compile", "--quiet", "-pl", moduleName, "--quiet").plus(pluginSettings.mavenUserArguments)
        )

    fun mavenwCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./mvnw", "compile", "--quiet"
            ).plus(mavenUserArguments)
        )
    }

    fun mavenwInstall(): CommandResult =
        shellCommandExecutor.execute(
            listOf(
                "./mvnw",
                "install",
                "-DskipTests",
                "--quiet"
            ).plus(pluginSettings.mavenUserArguments)
        )

    fun mavenwInstallModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(
            listOf("./mvnw", "install", "-DskipTests", "--quiet", "-pl", moduleName).plus(
                pluginSettings.mavenUserArguments
            )
        )

    fun mavenwCaptureInstall(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./mvnw", "install", "-DskipTests", "--quiet"
            ).plus(mavenUserArguments)
        )
    }
    //endregion

    //region maven
    fun mavenClean(): CommandResult =
        shellCommandExecutor.execute(listOf("mvn", "clean", "--quiet"))

    fun mavenCompile(): CommandResult =
        shellCommandExecutor.execute(listOf("mvn", "compile", "--quiet").plus(pluginSettings.mavenUserArguments))

    fun mavenCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(
            listOf(
                "mvn",
                "compile",
                "--quiet",
                "-pl",
                moduleName
            ).plus(pluginSettings.mavenUserArguments)
        )

    fun mavenCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "mvn", "compile", "--quiet"
            ).plus(mavenUserArguments)
        )
    }

    fun mavenInstall(): CommandResult =
        shellCommandExecutor.execute(
            listOf(
                "mvn",
                "install",
                "-DskipTests",
                "--quiet"
            ).plus(pluginSettings.mavenUserArguments)
        )

    fun mavenInstallModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(
            listOf("mvn", "install", "-DskipTests", "--quiet", "-pl", moduleName).plus(
                pluginSettings.mavenUserArguments
            )
        )

    fun mavenCaptureInstall(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "mvn", "install", "-DskipTests", "--quiet"
            ).plus(mavenUserArguments)
        )
    }
    //endregion

    //region gradleW
    fun gradlewClean(): CommandResult =
        shellCommandExecutor.execute(listOf("./gradlew", "clean", "--quiet"))

    fun gradlewCompile(): CommandResult =
        shellCommandExecutor.execute(
            listOf(
                "./gradlew",
                "build",
                "-x",
                "test",
                "--quiet"
            ).plus(pluginSettings.gradleUserArguments)
        )

    fun gradlewCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(
            listOf("./gradlew", ":$moduleName:build", "-x", "test", "--quiet").plus(
                pluginSettings.gradleUserArguments
            )
        )

    fun gradlewCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "./gradlew", "build", "-x", "test", "--quiet"
            ).plus(gradleUserArguments)
        )
    }
    //endregion

    //region gradle
    fun gradleClean(): CommandResult =
        shellCommandExecutor.execute(listOf("gradle", "clean", "--quiet"))

    fun gradleCompile(): CommandResult =
        shellCommandExecutor.execute(
            listOf(
                "gradle",
                "build",
                "-x",
                "test",
                "--quiet"
            ).plus(pluginSettings.gradleUserArguments)
        )

    fun gradleCompileModule(moduleName: String): CommandResult =
        shellCommandExecutor.execute(
            listOf(
                "gradle",
                ":$moduleName:build",
                "-x",
                "test",
                "--quiet"
            ).plus(pluginSettings.gradleUserArguments)
        )

    fun gradleCapture(): CommandResult = with(pluginSettings) {
        return shellCommandExecutor.execute(
            listOf(
                inferPath,
                "--results-dir", inferWorkingDir,
                "--no-progress-bar",
                "capture",
                "--",
                "gradle", "build", "-x", "test", "--quiet"
            ).plus(gradleUserArguments)
        )
    }
    //endregion
}
