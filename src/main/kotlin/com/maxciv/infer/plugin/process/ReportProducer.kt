package com.maxciv.infer.plugin.process

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.maxciv.infer.plugin.process.report.InferReport
import com.maxciv.infer.plugin.process.report.InferViolation
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
object ReportProducer {

    private val GSON = Gson()

    @Throws(FileNotFoundException::class)
    fun produceInferReport(projectPath: String): InferReport {
        val collectionType = object : TypeToken<Collection<InferViolation>>() {}.type
        val violations = GSON.fromJson<Collection<InferViolation>>(FileReader("$projectPath/infer-out/report.json"), collectionType)
        return InferReport(ArrayList(violations))
    }
}
