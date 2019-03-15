package com.maxciv.infer.plugin.data.report

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
data class InferReport(
    var violations: List<InferViolation> = mutableListOf()
)
