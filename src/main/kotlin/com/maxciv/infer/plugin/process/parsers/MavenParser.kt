package com.maxciv.infer.plugin.process.parsers

import com.maxciv.infer.plugin.data.ProjectModule

/**
 * @author maxim.oleynik
 * @since 10.03.2019
 */
object MavenParser {

    private val emptyLineRegex = """\n\n""".toRegex()
    private val argsBeginningRegex = """.*\+\+Contents of .*javac\.JavacCompiler\d*arguments.*""".toRegex()
    private val lastPathSeparatorRegex = """([:;])"""".toRegex()
    private val oneArgRegex = """".*?"""".toRegex()
    private val javaFilesRegex = """\.java$""".toRegex()

    fun getCompilerArgs(logs: String): List<ProjectModule> {
        // 1. Делим по пустым строкам все логи на блоки модулей
        val moduleBlocks = logs.split(emptyLineRegex)

        // 2. Для каждого модуля получаем список файлов и аргументов компилятора
        return moduleBlocks.asSequence()
            .map { moduleBlock ->
                val allCompilerArgs = moduleBlock.lines().asSequence()
                    .dropWhile { !it.contains(argsBeginningRegex) }
                    .drop(1)
                    .takeWhile { it.contains("\"") }
                    .map { it.replace(lastPathSeparatorRegex, "\"") }
                    .flatMap { oneArgRegex.findAll(it).map { it.value } }
                    .map { it.replace("\"", "") }
                    .toList()
                val fileList = allCompilerArgs.filter { it.contains(javaFilesRegex) }.toList()
                val compilerArgsList = allCompilerArgs.filterNot { it.contains(javaFilesRegex) }.toList()
                ProjectModule(fileList, compilerArgsList)
            }
            .filter { it.compilerArgs.isNotEmpty() && it.sourceFiles.isNotEmpty() }
            .toList()
    }
}
