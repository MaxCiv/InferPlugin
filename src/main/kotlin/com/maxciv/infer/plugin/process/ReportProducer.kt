package com.maxciv.infer.plugin.process

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

    fun produceInferReport(projectPath: String): InferReport {
        if (!File("$projectPath/infer-out/report.json").exists()) return InferReport()

        val collectionType = object : TypeToken<Collection<InferViolation>>() {}.type
        val violations = GSON.fromJson<Collection<InferViolation>>(FileReader("$projectPath/infer-out/report.json"), collectionType)
        return InferReport(violations.groupBy { it.file })
    }
}
