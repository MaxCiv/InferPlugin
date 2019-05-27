package com.maxciv.infer.plugin.process.shell

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
@Deprecated("Should use ShellCommandExecutorNuImpl")
class ShellCommandExecutorImpl(override val workingDirectory: File? = null) : ShellCommandExecutor {

    override fun execute(command: List<String>, environment: Map<String, String>, timeoutSec: Long): CommandResult {
        val process = startProcess(command, environment)
        process.waitFor(timeoutSec, TimeUnit.SECONDS)
        return CommandResult(
//            process.inputStream.bufferedReader().readText(),
//            process.errorStream.bufferedReader().readText(),
            "stdOut output disabled",
            "stdErr output disabled",
            process.exitValue(),
            cmd = command
        )
    }

    fun startProcess(command: List<String>, environment: Map<String, String>): Process {
        val processBuilder = ProcessBuilder(command)
            .directory(workingDirectory)
        processBuilder.environment().putAll(environment)
        return processBuilder.start()
    }
}
