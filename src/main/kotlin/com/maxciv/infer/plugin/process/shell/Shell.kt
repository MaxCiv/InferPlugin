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
            listOf(inferPath, "--reactive", "capture", "--", "javac", filename)
                .plus(compilerArgs)
        )
    }

    fun analyze(changedFilesIndex: File): CommandResult {
        return shellCommandExecutor.execute(
            listOf(inferPath, "analyze", "--changed-files-index", changedFilesIndex.canonicalPath)
        )
    }

    fun analyzeClassFiles(projectModule: ProjectModule): CommandResult {
        return shellCommandExecutor.execute(
            listOf(
                inferPath, "--classpath", projectModule.getClasspath(),
                "--sourcepath", projectModule.getSourcePath().split(":").first().trim(),
                "--generated-classes", projectModule.getGeneratedClasses()
            )
        )
    }

    fun analyzeAll(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "analyze"))
    }

    fun mavenClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("mvn", "clean"))
    }

    fun mavenCapture(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "capture", "--", "mvn", "compile"))
    }

    fun gradlewClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("./gradlew", "clean"))
    }

    fun gradlewCapture(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "capture", "--", "./gradlew", "build"))
    }

    fun gradleClean(): CommandResult {
        return shellCommandExecutor.execute(listOf("gradle", "clean"))
    }

    fun gradleCapture(): CommandResult {
        return shellCommandExecutor.execute(listOf(inferPath, "capture", "--", "gradle", "build"))
    }
}
