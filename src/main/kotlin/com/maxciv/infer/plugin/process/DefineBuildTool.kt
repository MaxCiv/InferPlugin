package com.maxciv.infer.plugin.process

import com.intellij.openapi.project.Project

/**
 * @author maxim.oleynik
 * @since 29.11.2018
 */
object DefineBuildTool {

    fun defineFor(project: Project): BuildTools {
        if (project.baseDir.findChild("pom.xml") != null) {
            return BuildTools.MAVEN
        }
        return if (project.baseDir.findChild("build.gradle") != null) {
            BuildTools.GRADLE
        } else BuildTools.DEFAULT
    }
}