package com.maxciv.infer.plugin.process.shell.nuproc

import com.maxciv.infer.plugin.process.shell.CommandResult
import com.maxciv.infer.plugin.process.shell.ShellCommandExecutor
import java.io.File
import java.time.Duration

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
class ShellCommandExecutorNuImpl(override val workingDirectory: File? = null) : ShellCommandExecutor {

    private val shellCommand: IShellCommand = ShellCommand(workingDirectory)

    override fun execute(command: List<String>, environment: Map<String, String>, timeoutSec: Long): CommandResult {
        return shellCommand.exec(command, environment, Duration.ofSeconds(1200L))
    }
}
