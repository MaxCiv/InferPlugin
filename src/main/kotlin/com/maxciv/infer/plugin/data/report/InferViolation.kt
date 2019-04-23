package com.maxciv.infer.plugin.data.report

import com.google.gson.annotations.SerializedName

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
class InferViolation {

    @SerializedName("bug_class")
    var bugClass: String = ""
    var kind: String = ""
    @SerializedName("bug_type")
    var bugType: String = ""
    var qualifier: String = ""
    var severity: String = ""
    var visibility: String = ""
    var line: Int = 0
    var column: Int = 0
    var procedure: String = ""
    @SerializedName("procedure_id")
    var procedureId: String = ""
    @SerializedName("procedure_start_line")
    var procedureStartLine: Int = 0
    var file: String = ""
    var key: String = ""
    @SerializedName("node_key")
    var nodeKey: String = ""
    var hash: String = ""
    @SerializedName("bug_type_hum")
    var bugTypeHum: String = ""
    @SerializedName("censored_reason")
    var censoredReason: String = ""
}
