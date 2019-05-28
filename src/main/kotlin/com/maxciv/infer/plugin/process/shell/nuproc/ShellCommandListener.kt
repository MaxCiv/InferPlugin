package com.maxciv.infer.plugin.process.shell.nuproc

import com.zaxxer.nuprocess.NuProcessHandler

interface ShellCommandListener : NuProcessHandler {
    val stdOut: String
    val stdErr: String
    val bytes: ByteArray
    val exitCode: Int
}
