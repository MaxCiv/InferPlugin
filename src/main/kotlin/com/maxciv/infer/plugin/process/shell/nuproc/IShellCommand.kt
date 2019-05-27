package com.maxciv.infer.plugin.process.shell.nuproc

import com.maxciv.infer.plugin.process.shell.CommandResult
import org.slf4j.Marker
import java.time.Duration

/**
 * @author maxim.oleynik
 * @since 27.05.2019
 */
interface IShellCommand {

    fun exec(
        command: List<String>,
        environment: Map<String, String> = mapOf(),
        timeOut: Duration = Duration.ofSeconds(60),
        returnFailure: Boolean = true,
        logMarker: Marker? = null,
        processListener: IShellCommandListener = ShellCommandListener()
    ): CommandResult

    fun startProcess(
        command: List<String>,
        environment: Map<String, String>,
        logMarker: Marker? = null,
        processListener: LongRunningProcessListener
    )
}
