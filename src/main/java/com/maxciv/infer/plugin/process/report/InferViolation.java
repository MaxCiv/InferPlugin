package com.maxciv.infer.plugin.process.report;

import com.google.gson.annotations.SerializedName;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class InferViolation {

    @SerializedName("bug_class")
    private String bugClass;
    private String kind;
    @SerializedName("bug_type")
    private String bugType;
    private String qualifier;
    private String severity;
    private String visibility;
    private int line;
    private int column;
    private String procedure;
    @SerializedName("procedure_id")
    private String procedureId;
    @SerializedName("procedure_start_line")
    private int procedureStartLine;
    private String file;
    private String key;
    @SerializedName("node_key")
    private String nodeKey;
    private String hash;
    @SerializedName("bug_type_hum")
    private String bugTypeHum;
    @SerializedName("censored_reason")
    private String censoredReason;

    public InferViolation() {
    }

    public String getBugClass() {
        return bugClass;
    }

    public void setBugClass(String bugClass) {
        this.bugClass = bugClass;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getBugType() {
        return bugType;
    }

    public void setBugType(String bugType) {
        this.bugType = bugType;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public int getProcedureStartLine() {
        return procedureStartLine;
    }

    public void setProcedureStartLine(int procedureStartLine) {
        this.procedureStartLine = procedureStartLine;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBugTypeHum() {
        return bugTypeHum;
    }

    public void setBugTypeHum(String bugTypeHum) {
        this.bugTypeHum = bugTypeHum;
    }

    public String getCensoredReason() {
        return censoredReason;
    }

    public void setCensoredReason(String censoredReason) {
        this.censoredReason = censoredReason;
    }
}
