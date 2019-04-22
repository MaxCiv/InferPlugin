package com.maxciv.infer.plugin.data

/**
 * Модуль проекта, содержить список файлов с исходным кодом и список аргументов компилятора
 *
 * @author maxim.oleynik
 * @since 10.04.2019
 */
data class ProjectModule(
    var sourceFiles: List<String> = mutableListOf(),
    var compilerArgs: List<String> = mutableListOf()
) {

    fun getSourcePath(): String = getNextAfter("-sourcepath")

    fun getClasspath(): String = getNextAfter("-classpath")

    fun getGeneratedClasses(): String = getNextAfter("-d")

    private fun getNextAfter(element: String): String {
        val index = compilerArgs.indexOf(element)
        return if (index == -1 || compilerArgs.size < index + 2) "&" else compilerArgs[index + 1]
    }
}
