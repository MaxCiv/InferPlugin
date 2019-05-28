package com.maxciv.infer.plugin

import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import java.io.File

/**
 * @author maxim.oleynik
 * @since 14.05.2019
 */

fun ProgressIndicator?.updateText(newText: String, progress: Double = -1.0) {
    val progressIndicator = this ?: ProgressIndicatorProvider.getGlobalProgressIndicator()
    progressIndicator?.apply {
        text = newText
        if (progress in 0.0..1.0) {
            isIndeterminate = false
            fraction = progress
        }
    }
}

/**
 * From '/root/some/src/java/...' to 'src/java/...'
 * with projectPath = '/root/some'
 */
fun String.toProjectRelativePath(projectPath: String): String = this.replace(projectPath + File.separator, "")

fun Module.realName(): String = this.name.replace("""_(main|test)$""".toRegex(), "")
