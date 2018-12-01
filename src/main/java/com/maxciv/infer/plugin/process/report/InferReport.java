package com.maxciv.infer.plugin.process.report;

import java.util.List;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class InferReport {

    private List<InferViolation> violations;

    public InferReport(List<InferViolation> violations) {
        this.violations = violations;
    }

    public List<InferViolation> getViolations() {
        return violations;
    }

    public void setViolations(List<InferViolation> violations) {
        this.violations = violations;
    }
}
