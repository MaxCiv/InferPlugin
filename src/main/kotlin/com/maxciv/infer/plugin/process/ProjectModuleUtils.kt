package com.maxciv.infer.plugin.process

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.maxciv.infer.plugin.data.ProjectModule
import java.io.File

/**
 * @author maxim.oleynik
 * @since 22.04.2019
 */
object ProjectModuleUtils {

    /**
     * Вернуть модуль, к которому относится файл
     */
    fun getModuleForFile(filename: String, projectModules: List<ProjectModule>): ProjectModule {
        // Если модуль только один, то берём сразу его
        if (projectModules.count() == 1) return projectModules[0]

        // Если модулей несколько, ищем файл во всех модулях
        val requiredModule = projectModules.asSequence()
            .filter { it.sourceFiles.contains(filename) }
            .toList()

        // Если нашли файл по точному совпадению в одном из модулей, возвращаем его
        if (requiredModule.isNotEmpty()) return requiredModule[0]

        // Если файл новый, проверим какому -sourcepath какого модуля он соответствует и вернём его
        val possibleModule = projectModules.asSequence()
            .filter {
                it.getSourcePath()
                    .split(File.pathSeparator)
                    .any { filename.contains(it) }
            }
            .toList()

        // Если подходящего модуля не нашлось, возвращаем пустой
        return if (possibleModule.isNotEmpty()) possibleModule[0] else ProjectModule(listOf(), listOf())
    }

    fun getInferWorkingDirForModule(inferWorkingDir: String, module: ProjectModule): String {
        return inferWorkingDir + "_" + module.hashCode()
    }

    fun getIdeaModuleForFile(filename: String, project: Project): Module? {
        return ModuleUtil.findModuleForFile(LocalFileSystem.getInstance().findFileByIoFile(File(filename))!!, project)
    }
}
