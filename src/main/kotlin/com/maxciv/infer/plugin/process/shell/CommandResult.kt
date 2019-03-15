package com.maxciv.infer.plugin.process.shell

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
data class CommandResult(
    val stdOut: String,
    val stdErr: String,
    val exitCode: Int,
    val cmd: List<String> = listOf(),
    val isSuccess: Boolean = exitCode == 0
)
