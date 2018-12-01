package com.maxciv.infer.plugin.config;

import com.maxciv.infer.plugin.process.BuildTools;

/**
 * @author maxim.oleynik
 * @since 27.11.2018
 */
public class InferPluginSettings {

    private String inferPath = "infer";
    private BuildTools buildTool = BuildTools.DEFAULT;

    InferPluginSettings() {
    }

    public String getInferPath() {
        return inferPath;
    }

    public void setInferPath(String inferPath) {
        this.inferPath = inferPath;
    }

    public BuildTools getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(BuildTools buildTool) {
        this.buildTool = buildTool;
    }
}
