package com.maxciv.infer.plugin.data.report

/**
 * Полный отчет Infer, для каждого файла содержит список нарушений
 *
 * @author maxim.oleynik
 * @since 01.12.2018
 */
data class InferReport(
    var violationsByFile: MutableMap<String, List<InferViolation>> = mutableMapOf()
) {
    fun updateForFile(filename: String, violations: List<InferViolation>) {
        if (violations.isEmpty()) {
            violationsByFile.remove(filename)
        } else {
            violationsByFile[filename] = violations
        }
    }
}
