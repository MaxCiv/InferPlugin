package com.maxciv.infer.plugin.tree;

import com.intellij.ui.SimpleTextAttributes;
import com.maxciv.infer.plugin.process.report.InferReport;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class RootNode extends DefaultMutableTreeNode implements TreeNodeData {

    private static final String LABEL = "Infer Results";

    private InferReport inferReport;

    RootNode() {
        super(LABEL);
    }

    public RootNode(String nodeName) {
        super(nodeName);
    }

    RootNode(InferReport inferReport) {
        this.inferReport = inferReport;
    }

    @Override
    public void render(CellRenderer cellRenderer) {
        cellRenderer.append(LABEL, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        if (inferReport != null) {
            cellRenderer.append(" (" + String.valueOf(inferReport.getViolations().size()) + " violations)",
                    SimpleTextAttributes.GRAYED_ATTRIBUTES);
        }
    }

    public InferReport getInferReport() {
        return inferReport;
    }

    public void setInferReport(InferReport inferReport) {
        this.inferReport = inferReport;
    }
}
