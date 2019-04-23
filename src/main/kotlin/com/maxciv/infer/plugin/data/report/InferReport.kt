package com.maxciv.infer.plugin.data.report

/**
 * Полный отчет Infer, для каждого файла содержит список нарушений
 *
 * @author maxim.oleynik
 * @since 01.12.2018
 */
data class InferReport(
    var violationsByFile: Map<String, List<InferViolation>> = mutableMapOf()
)
