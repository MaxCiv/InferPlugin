package com.maxciv.infer.plugin.process.report

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.maxciv.infer.plugin.data.report.InferReport
import com.maxciv.infer.plugin.data.report.InferViolation
import java.io.File
import java.io.FileReader

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
object ReportProducer {

    private val GSON = Gson()

    fun produceInferReport(projectPath: String, inferWorkingDirForModule: String): InferReport {
        if (!File("$inferWorkingDirForModule/report.json").exists()) return InferReport()

        val collectionType = object : TypeToken<Collection<InferViolation>>() {}.type
        val violations = GSON.fromJson<Collection<InferViolation>>(FileReader("$inferWorkingDirForModule/report.json"), collectionType)
        return InferReport(violations.groupBy { it.file }.toMutableMap())
            .also { fillViolationOffsets(it, projectPath) }
    }

    private fun fillViolationOffsets(inferReport: InferReport, projectPath: String) {
        inferReport.violationsByFile.keys.forEach { file ->
            val fileLines = File(projectPath + File.separator + file).readLines()
            inferReport.violationsByFile[file].orEmpty().forEach { violation ->
                violation.offset = fileLines.take(violation.line)
                    .map { it.length }
                    .sum() + violation.line - 1 // include BreakLines except the last one
            }
        }
    }
}
