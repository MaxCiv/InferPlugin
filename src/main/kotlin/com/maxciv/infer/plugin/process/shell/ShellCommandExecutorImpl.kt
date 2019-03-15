package com.maxciv.infer.plugin.process.shell

import java.io.File

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
class ShellCommandExecutorImpl(override val workingDirectory: File? = null) : ShellCommandExecutor {

    override fun execute(command: List<String>, environment: Map<String, String>): CommandResult {
        val process = startProcess(command, environment)
        val exitCode = process.waitFor()
        return CommandResult(process.inputStream.toString(), process.errorStream.toString(), exitCode, command)
    }

    override fun startProcess(command: List<String>, environment: Map<String, String>): Process {
        val processBuilder = ProcessBuilder(command)
            .directory(workingDirectory)
        processBuilder.environment().putAll(environment)
        return processBuilder.start()
    }
}
