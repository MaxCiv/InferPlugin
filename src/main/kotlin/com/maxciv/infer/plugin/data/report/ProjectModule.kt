package com.maxciv.infer.plugin.data.report

/**
 * Модуль проекта, содержить список файлов с исходным кодом и список аргументов компилятора
 *
 * @author maxim.oleynik
 * @since 10.04.2019
 */
data class ProjectModule(
    val sourceFiles: List<String>,
    val compilerArgs: List<String>
)
