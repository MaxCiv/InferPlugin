package com.maxciv.infer.plugin.process

import com.maxciv.infer.plugin.process.report.InferReport
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * @author maxim.oleynik
 * @since 30.11.2018
 */
class InferRunner(var projectPath: String, var inferPath: String) {

    @Throws(Exception::class)
    fun runAnalysis(buildTool: BuildTools): InferReport? {
        when (buildTool) {
            BuildTools.MAVEN -> {
                mavenClean()
                mavenRun()
                return ReportProducer.produceInferReport(projectPath)
            }
            BuildTools.GRADLE -> {
                gradleClean()
                gradleRun()
                return ReportProducer.produceInferReport(projectPath)
            }
            else -> {
            }
        }
        return null
    }

    @Throws(Exception::class)
    private fun mavenClean(): Int {
        return runCommand("mvn", "clean")
    }

    @Throws(Exception::class)
    private fun mavenRun(): Int {
        return runCommand(inferPath, "run", "--", "mvn", "compile")
    }

    @Throws(Exception::class)
    private fun gradleClean(): Int {
        return runCommand("./gradlew", "clean")
    }

    @Throws(Exception::class)
    private fun gradleRun(): Int {
        return runCommand(inferPath, "run", "--", "./gradlew", "build")
    }

    @Throws(Exception::class)
    private fun runCommand(vararg command: String): Int {
        val procBuilder = ProcessBuilder(*command)
                .directory(File(projectPath))
        procBuilder.redirectErrorStream(true)

        val process = procBuilder.start()

        val stdout = process.inputStream
        val brStdout = BufferedReader(InputStreamReader(stdout))

        return process.waitFor()
    }
}
