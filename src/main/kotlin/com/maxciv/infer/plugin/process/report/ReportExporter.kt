package com.maxciv.infer.plugin.process.report

import com.google.gson.Gson
import com.maxciv.infer.plugin.data.report.InferReport
import java.io.File

/**
 * @author maxim.oleynik
 * @since 15.05.2019
 */
object ReportExporter {

    private val GSON = Gson()

    fun exportReport(projectPath: String, inferReport: InferReport) {
        val jsonString = GSON.toJson(inferReport)
        File("$projectPath/inferPluginReport.json").printWriter().apply {
            println(jsonString)
            flush()
        }
    }
}
