package com.maxciv.infer.plugin.process.shell

import java.io.File

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
interface ShellCommandExecutor {

    val workingDirectory: File?

    fun execute(command: List<String>, environment: Map<String, String> = mapOf(), timeoutSec: Long = 1200L): CommandResult
//    fun startProcess(command: List<String>, environment: Map<String, String> = mapOf()): NuProcess
//    fun startProcess(command: List<String>, environment: Map<String, String> = mapOf()): Process
}
