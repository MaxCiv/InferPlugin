package com.maxciv.infer.plugin.process;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.maxciv.infer.plugin.process.report.InferReport;
import com.maxciv.infer.plugin.process.report.InferViolation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public final class ReportProducer {

    private static final Gson GSON = new Gson();

    private ReportProducer() {
    }

    public static InferReport produceInferReport(String projectPath) throws FileNotFoundException {
        Type collectionType = new TypeToken<Collection<InferViolation>>(){}.getType();
        Collection<InferViolation> violations = GSON.fromJson(new FileReader(projectPath + "/infer-out/report.json"), collectionType);
        return new InferReport(new ArrayList(violations));
    }
}
