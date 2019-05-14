package com.maxciv.infer.plugin.process.shell

import com.maxciv.infer.plugin.data.ProjectModule
import java.io.File

/**
 * Консольные команды
 *
 * @author maxim.oleynik
 * @since 22.04.2019
 */
class Shell(
    private val shellCommandExecutor: ShellCommandExecutor,
    private val inferPath: String
) {
    fun javac(filename: String, compilerArgs: List<String>): CommandResult {
        return shellCommandExecutor.execute(
            listOf(inferPath, "--no-progress-bar", "--reactive", "capture", "--", "javac", filename)
                .plus(compilerArgs)
        )
    }

    fun analyze(changedFilesIndex: File): CommandResult {
        return shellCommandExecutor.execute(
            listOf(inferPath, "--no-progress-bar", "analyze", "--changed-files-index", changedFilesIndex.canonicalPath)
        )
    }

    fun analyzeClassFiles(projectModule: ProjectModule): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                inferPath, "--no-progress-bar", "--classpath", projectModule.getClasspath(),
                "--sourcepath", projectModule.getSourcePath().split(":").first().trim(),
                "--generated-classes", projectModule.getGeneratedClasses()
            )
        )
    }

    fun analyzeAll(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "--no-progress-bar", "analyze"))
    }

    fun mavenClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("mvn", "clean"))
    }

    fun mavenCompile(): CommandResult {
        return shellCommandExecutor.execute(listOf("mvn", "compile"))
    }

    fun mavenCapture(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "--no-progress-bar", "capture", "--", "mvn", "compile"))
    }

    fun gradlewClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("./gradlew", "clean"))
    }

    fun gradlewCompile(): CommandResult {
        return shellCommandExecutor.execute(listOf("./gradlew", "build"))
    }

    fun gradlewCapture(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "--no-progress-bar", "capture", "--", "./gradlew", "build"))
    }

    fun gradleClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("gradle", "clean"))
    }

    fun gradleCompile(): CommandResult {
        return shellCommandExecutor.execute(listOf("gradle", "build"))
    }

    fun gradleCapture(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "--no-progress-bar", "capture", "--", "gradle", "build"))
    }
}
