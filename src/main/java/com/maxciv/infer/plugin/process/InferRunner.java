package com.maxciv.infer.plugin.process;

import com.maxciv.infer.plugin.process.report.InferReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author maxim.oleynik
 * @since 30.11.2018
 */
public class InferRunner {

    private String projectPath;
    private String inferPath;

    public InferRunner(String projectPath, String inferPath) {
        this.projectPath = projectPath;
        this.inferPath = inferPath;
    }

    public InferReport runAnalysis(BuildTools buildTool) throws Exception {
        switch (buildTool) {
            case MAVEN:
                mavenClean();
                mavenRun();
                return ReportProducer.produceInferReport(projectPath);
            case GRADLE:
                gradleClean();
                gradleRun();
                return ReportProducer.produceInferReport(projectPath);
            default:
                break;
        }
        return null;
    }

    private int mavenClean() throws Exception {
        return runCommand("mvn", "clean");
    }

    private int mavenRun() throws Exception {
        return runCommand(inferPath, "run", "--", "mvn", "compile");
    }

    private int gradleClean() throws Exception {
        return runCommand("./gradlew", "clean");
    }

    private int gradleRun() throws Exception {
        return runCommand(inferPath, "run", "--", "./gradlew", "build");
    }

    private int runCommand(String... command) throws Exception {
        ProcessBuilder procBuilder = new ProcessBuilder(command)
                .directory(new File(projectPath));
        procBuilder.redirectErrorStream(true);

        Process process = procBuilder.start();

        InputStream stdout = process.getInputStream();
        BufferedReader brStdout = new BufferedReader(new InputStreamReader(stdout));

        return process.waitFor();
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getInferPath() {
        return inferPath;
    }

    public void setInferPath(String inferPath) {
        this.inferPath = inferPath;
    }
}
