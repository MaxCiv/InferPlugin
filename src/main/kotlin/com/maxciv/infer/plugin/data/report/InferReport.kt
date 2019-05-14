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
    fun updateForFile(filename: String, violations: List<InferViolation>) {
        if (violations.isEmpty()) {
            violationsByFile.remove(filename)
        } else {
            violationsByFile[filename] = violations
        }
    }

    fun updateForModuleReport(inferReport: InferReport, projectModule: ProjectModule, projectPath: String) {
        projectModule.sourceFiles.asSequence()
            .map { it.toProjectRelativePath(projectPath) }
            .forEach { filename ->
                updateForFile(filename, inferReport.violationsByFile[filename].orEmpty())
            }
    }

    fun getTotalViolationCount(): Int {
        return violationsByFile.values.sumBy { it.count() }
    }
}
