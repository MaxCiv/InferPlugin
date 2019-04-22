package com.maxciv.infer.plugin.process.parsers

import com.maxciv.infer.plugin.data.ProjectModule
import java.io.File

/**
 * @author maxim.oleynik
 * @since 14.03.2019
 */
object GradleParser {

    // for compiler arguments
    private val emptyLineRegex = """\n\n""".toRegex()
    private val loggerMarkerRegex = """\[.*?]\s*""".toRegex()
    private val javacRegex = """.*javac\s*""".toRegex()
    private val javaFilesRegex = """@.*/gradle.*txt""".toRegex()
    private val javacOptionRegex = """(\s|^)-\S+""".toRegex()
    private val spaceAtBeginningRegex = """^\s.+""".toRegex()
    private val sourcepathRegex = """.*/(main|test)/java""".toRegex()
    private const val DELIMETER = "\uD83D\uDE31"

    // for file list
    private val fileListBeginningRegex = """.*\+\+Contents of .*filelists/gradle.*\.txt""".toRegex()


    fun getCompilerArgs(logs: String): List<ProjectModule> {
        // 1. Делим по пустым строкам все логи на блоки модулей
        val moduleBlocks = logs.split(emptyLineRegex)

        // 2. Для каждого модуля получаем список файлов и аргументов компилятора
        return moduleBlocks.asSequence()
            .map { moduleBlock ->
                val filenamesList = getFilenames(moduleBlock.lines())
                val compilerArgsList = getCompilerArgs(moduleBlock.lines())
                val updatedCompilerArgs =
                    if (compilerArgsList.isNotEmpty() && filenamesList.isNotEmpty())
                        updateCompilerArgsForFile(filenamesList[0], compilerArgsList)
                    else compilerArgsList
                ProjectModule(filenamesList, updatedCompilerArgs)
            }
            .filter { it.compilerArgs.isNotEmpty() && it.sourceFiles.isNotEmpty() }
            .toList()
    }

    private fun getFilenames(logLines: List<String>): List<String> {
        return logLines.asSequence()
            .map { it.replace(loggerMarkerRegex, "") }
            .dropWhile { !it.contains(fileListBeginningRegex) }
            .drop(1)
            .takeWhile { it.isNotBlank() }
            .toList()
    }

    private fun getCompilerArgs(logLines: List<String>): List<String> {
        return logLines.asSequence()
            .map { it.replace(loggerMarkerRegex, "") }
            .dropWhile { !it.contains(javacRegex) }
            .takeWhile { !it.contains(javaFilesRegex) }
            .map { it.replace(javacRegex, "") }
            .flatMap { fullLine ->
                var changedLine = fullLine
                javacOptionRegex.findAll(fullLine)
                    .forEach {
                        changedLine = changedLine.replace(it.value, "$DELIMETER${it.value}$DELIMETER")
                    }
                changedLine.replace("^\uD83D\uDE31".toRegex(), "")
                    .replace("$DELIMETER$".toRegex(), "")
                    .split(DELIMETER).asSequence()
            }
            .filter { it.isNotEmpty() }
            .map { if (it.contains(spaceAtBeginningRegex)) it.drop(1) else it }
            .toList()
    }

    private fun updateCompilerArgsForFile(filename: String, compilerArgs: List<String>): List<String> {
        val sourcepath = sourcepathRegex.find(filename)!!.value

        val newArgs = compilerArgs.toMutableList()
        val indexOfDestination = newArgs.indexOf("-d")
        val indexOfSourcepath = newArgs.indexOf("-sourcepath")
        val indexOfClasspath = newArgs.indexOf("-classpath")

        val classpathAddition =
            newArgs[indexOfDestination + 1] + if (newArgs[indexOfClasspath + 1].isNotBlank()) File.pathSeparator else ""
        newArgs[indexOfClasspath + 1] = classpathAddition + newArgs[indexOfClasspath + 1]

        val sourcepathAddition =
            sourcepath + if (newArgs[indexOfSourcepath + 1].isNotBlank()) File.pathSeparator else ""
        newArgs[indexOfSourcepath + 1] = sourcepathAddition + newArgs[indexOfSourcepath + 1]

        return newArgs
    }
}
