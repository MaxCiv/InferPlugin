package com.maxciv.infer.plugin

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import java.io.File

/**
 * @author maxim.oleynik
 * @since 14.05.2019
 */

fun ProgressIndicator?.updateText(newText: String) {
    val progressIndicator = this ?: ProgressIndicatorProvider.getGlobalProgressIndicator()
    progressIndicator?.apply { text = newText  }
}

/**
 * From '/root/some/src/java/...' to 'src/java/...'
 * with projectPath = '/root/some'
 */
fun String.toProjectRelativePath(projectPath: String): String {
    return this.replace(projectPath + File.separator, "")
}
