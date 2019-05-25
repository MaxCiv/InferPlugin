package com.maxciv.infer.plugin.data.report

import com.maxciv.infer.plugin.data.ProjectModule
import com.maxciv.infer.plugin.toProjectRelativePath

/**
 * Полный отчет Infer, для каждого файла содержит список нарушений
 *
 * @author maxim.oleynik
 * @since 01.12.2018
 */
data class InferReport(
    var violationsByFile: MutableMap<String, List<InferViolation>> = mutableMapOf()
) {
    fun getTotalViolationCount(): Int = violationsByFile.values.sumBy { it.count() }

    fun updateForModuleReport(inferReport: InferReport, projectModule: ProjectModule, projectPath: String) =
        projectModule.sourceFiles.asSequence()
            .map { it.toProjectRelativePath(projectPath) }
            .forEach { filename ->
                updateForFile(filename, inferReport.violationsByFile[filename].orEmpty())
            }

    fun updateForFiles(filenames: List<String>, inferReport: InferReport, projectPath: String) =
        filenames.map { it.toProjectRelativePath(projectPath) }.forEach { filename ->
            val violations = inferReport.violationsByFile.getOrDefault(filename, listOf())
            updateForFile(filename, violations)
        }

    private fun updateForFile(filename: String, violations: List<InferViolation>) {
        if (violations.isEmpty()) {
            violationsByFile.remove(filename)
        } else {
            violationsByFile[filename] = violations
        }
    }
}
