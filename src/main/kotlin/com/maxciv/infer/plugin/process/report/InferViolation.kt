package com.maxciv.infer.plugin.process.report

import com.google.gson.annotations.SerializedName

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class InferViolation {

    @SerializedName("bug_class")
    var bugClass: String? = null
    var kind: String? = null
    @SerializedName("bug_type")
    var bugType: String? = null
    var qualifier: String? = null
    var severity: String? = null
    var visibility: String? = null
    var line: Int = 0
    var column: Int = 0
    var procedure: String? = null
    @SerializedName("procedure_id")
    var procedureId: String? = null
    @SerializedName("procedure_start_line")
    var procedureStartLine: Int = 0
    var file: String? = null
    var key: String? = null
    @SerializedName("node_key")
    var nodeKey: String? = null
    var hash: String? = null
    @SerializedName("bug_type_hum")
    var bugTypeHum: String? = null
    @SerializedName("censored_reason")
    var censoredReason: String? = null
}
