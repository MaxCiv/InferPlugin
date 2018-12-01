package com.maxciv.infer.plugin.process;

import com.intellij.openapi.project.Project;

/**
 * @author maxim.oleynik
 * @since 29.11.2018
 */
public final class DefineBuildTool {

    private DefineBuildTool() {
    }

    public static BuildTools defineFor(Project project) {
        if (project.getBaseDir().findChild("pom.xml") != null) {
            return BuildTools.MAVEN;
        }
        if (project.getBaseDir().findChild("build.gradle") != null) {
            return BuildTools.GRADLE;
        }
        return BuildTools.DEFAULT;
    }
}
