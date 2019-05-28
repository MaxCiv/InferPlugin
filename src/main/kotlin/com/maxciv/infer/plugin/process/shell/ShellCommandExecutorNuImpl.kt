package com.maxciv.infer.plugin.process.shell

import com.maxciv.infer.plugin.process.shell.nuproc.ShellCommand
import com.maxciv.infer.plugin.process.shell.nuproc.ShellCommandImpl
import java.io.File
import java.time.Duration

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
class ShellCommandExecutorNuImpl(override val workingDirectory: File? = null) : ShellCommandExecutor {

    private val shellCommand: ShellCommand = ShellCommandImpl(workingDirectory)

    override fun execute(command: List<String>, environment: Map<String, String>, timeoutSec: Long): CommandResult {
        return shellCommand.exec(command, environment, Duration.ofSeconds(1200L))
    }
}
