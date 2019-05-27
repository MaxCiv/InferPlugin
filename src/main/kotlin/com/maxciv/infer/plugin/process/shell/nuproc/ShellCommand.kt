package com.maxciv.infer.plugin.process.shell.nuproc

import com.maxciv.infer.plugin.process.shell.CommandResult
import com.zaxxer.nuprocess.NuProcess
import com.zaxxer.nuprocess.NuProcessBuilder
import com.zaxxer.nuprocess.NuProcessHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import java.io.File
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author maxim.oleynik
 * @since 27.05.2019
 */
open class ShellCommand(
    private val workingDirectory: File?,
    private val builderFactory: (cmd: List<String>, env: Map<String, String>) -> NuProcessBuilder = ::defaultNuProcessBuilder,
    private val commonEnvironment: Map<String, String> = TreeMap(System.getenv())
) : IShellCommand {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass.simpleName)

    companion object {
        fun defaultNuProcessBuilder(cmd: List<String>, env: Map<String, String>): NuProcessBuilder =
            NuProcessBuilder(cmd, env)
    }

    override fun exec(
        command: List<String>, environment: Map<String, String>, timeOut: Duration,
        returnFailure: Boolean, logMarker: Marker?, processListener: IShellCommandListener
    ): CommandResult {
        val process: NuProcess = startProcessInternal(command, environment, processListener)

        var exitCode = Int.MIN_VALUE

        try {
            exitCode = process.waitFor(timeOut.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            logger.warn(logMarker, "Error while running command: ${command.joinToString(" ")}", e)
        }

        if (exitCode == Int.MIN_VALUE) { // waiting timed out
            try {
                process.destroy(true)
            } catch (e: RuntimeException) {
                logger.warn(logMarker, "Error while terminating command: ${command.joinToString(" ")}", e)
            }
        }

        return CommandResult(
            stdOut = processListener.stdOut,
            stdErr = processListener.stdErr,
            exitCode = processListener.exitCode,
            cmd = command
        )
    }

    override fun startProcess(
        command: List<String>,
        environment: Map<String, String>,
        logMarker: Marker?,
        processListener: LongRunningProcessListener
    ) {
        startProcessInternal(command, environment, processListener)
    }

    private fun startProcessInternal(
        command: List<String>,
        environment: Map<String, String>,
        processListener: NuProcessHandler
    ): NuProcess {
        val cmdEnv = environment + commonEnvironment
        val processBuilder = builderFactory(command, cmdEnv)
        workingDirectory?.let { processBuilder.setCwd(it.toPath()) }
        processBuilder.setProcessListener(processListener)
        return processBuilder.start()
    }
}
