package com.maxciv.infer.plugin.process.parsers

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
}
