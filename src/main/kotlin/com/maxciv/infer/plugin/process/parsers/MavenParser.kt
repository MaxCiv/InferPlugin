package com.maxciv.infer.plugin.process.parsers

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
object MavenParser {

    fun getCompilerArgs(logLines: List<String>): List<String> {
        val argsBeginningRegex = """.*\+\+Contents of .*javac\.JavacCompiler\d*arguments.*""".toRegex()
        val lastPathSeparatorRegex = """([:;])"""".toRegex()
        val oneArgRegex = """".*?"""".toRegex()
        val javaFilesRegex = """\.java"$""".toRegex()

        return logLines.asSequence()
            .dropWhile { !it.contains(argsBeginningRegex) }
            .drop(1)
            .takeWhile { it.contains("\"") }
            .map { it.replace(lastPathSeparatorRegex, "\"") }
            .flatMap {
                oneArgRegex.findAll(it)
                    .map { it.value }
            }
            .filterNot { it.contains(javaFilesRegex) }
            .map { it.replace("\"", "") }
            .toList()
    }
}
