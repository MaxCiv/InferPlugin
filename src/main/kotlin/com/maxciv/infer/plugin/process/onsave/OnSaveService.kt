package com.maxciv.infer.plugin.process.onsave

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager

/**
 * @author maxim.oleynik
 * @since 27.05.2019
 */
class OnSaveService(project: Project) {

    private val onSaveAnalyzeListener: OnSaveAnalyzeListener = OnSaveAnalyzeListener(project)

    fun registerOnSaveListener() = VirtualFileManager.getInstance().addVirtualFileListener(onSaveAnalyzeListener)

    fun unregisterOnSaveListener() = VirtualFileManager.getInstance().removeVirtualFileListener(onSaveAnalyzeListener)

    companion object {
        fun getInstance(project: Project): OnSaveService {
            return ServiceManager.getService(project, OnSaveService::class.java)
        }
    }
}
