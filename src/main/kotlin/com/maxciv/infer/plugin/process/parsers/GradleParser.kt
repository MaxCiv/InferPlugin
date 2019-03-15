package com.maxciv.infer.plugin.process.parsers

import java.io.File

/**
 * @author maxim.oleynik
 * @since 14.03.2019
 */
object GradleParser {

    fun getCompilerArgs(logLines: List<String>): List<String> {
        val loggerMarkerRegex = """\[.*?]\s*""".toRegex()
        val javacRegex = """.*javac\s*""".toRegex()
        val javaFilesRegex = """@.*/gradle.*txt""".toRegex()
        val javacOptionRegex = """(\s|^)-\S+""".toRegex()
        val spaceAtBeginningRegex = """^\s.+""".toRegex()
        val delimiter = "\uD83D\uDE31"

        return logLines.asSequence()
            .map { it.replace(loggerMarkerRegex, "") }
            .dropWhile { !it.contains(javacRegex) }
            .takeWhile { !it.contains(javaFilesRegex) }
            .map { it.replace(javacRegex, "") }
            .flatMap { fullLine ->
                var changedLine = fullLine
                javacOptionRegex.findAll(fullLine)
                    .forEach { changedLine = changedLine.replace(it.value, "$delimiter${it.value}$delimiter") }
                changedLine.replace("^\uD83D\uDE31".toRegex(), "")
                    .replace("$delimiter$".toRegex(), "")
                    .split(delimiter).asSequence()
            }
            .filter { it.isNotEmpty() }
            .map { if (it.contains(spaceAtBeginningRegex)) it.drop(1) else it }
            .toList()
    }

    fun updateCompilerArgsForFile(filename: String, compilerArgs: List<String>): List<String> {
        val sourcepathRegex = """.*/main/java""".toRegex()
        val sourcepath = sourcepathRegex.find(filename)!!.value

        val newArgs = compilerArgs.toMutableList()
        val indexOfDestination = newArgs.indexOf("-d")
        val indexOfSourcepath = newArgs.indexOf("-sourcepath")
        val indexOfClasspath = newArgs.indexOf("-classpath")

        val classpathAddition = newArgs[indexOfDestination + 1] + if (newArgs[indexOfClasspath + 1].isNotBlank()) File.pathSeparator else ""
        newArgs[indexOfClasspath + 1] = classpathAddition + newArgs[indexOfClasspath + 1]

        val sourcepathAddition = sourcepath + if (newArgs[indexOfSourcepath + 1].isNotBlank()) File.pathSeparator else ""
        newArgs[indexOfSourcepath + 1] = sourcepathAddition + newArgs[indexOfSourcepath + 1]

        return newArgs
    }
}
