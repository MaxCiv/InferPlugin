package com.maxciv.infer.plugin.process.report

import com.google.gson.Gson
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.data.report.InferReport
import java.io.File
import java.io.FileReader

/**
 * @author maxim.oleynik
 * @since 15.05.2019
 */
object ReportImporter {

    private val GSON = Gson()

    fun importReport(project: Project, pluginSettings: InferPluginSettings) {
        val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
        val file = FileChooser.chooseFile(
            descriptor,
            project,
            LocalFileSystem.getInstance().findFileByIoFile(File(project.basePath))
        ) ?: return
        val inferReport = GSON.fromJson<InferReport>(FileReader(file.canonicalPath), InferReport::class.java)
        pluginSettings.aggregatedInferReport = inferReport
    }
}
