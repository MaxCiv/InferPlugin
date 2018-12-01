package com.maxciv.infer.plugin.tree;

import com.intellij.ui.SimpleTextAttributes;
import com.maxciv.infer.plugin.process.report.InferViolation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class ViolationNode extends DefaultMutableTreeNode implements TreeNodeData {

    private InferViolation violation;

    public ViolationNode(InferViolation violation) {
        this.violation = violation;
    }

    @Override
    public void render(CellRenderer cellRenderer) {
        cellRenderer.append("(" + violation.getLine() + ", " + violation.getColumn() + ") ",
                SimpleTextAttributes.GRAYED_ATTRIBUTES);
        cellRenderer.append(violation.getBugTypeHum() + " - " + violation.getQualifier(),
                SimpleTextAttributes.REGULAR_ATTRIBUTES);
        cellRenderer.append(" (" + violation.getFile() + ")",
                SimpleTextAttributes.GRAYED_ATTRIBUTES);
    }

    public InferViolation getViolation() {
        return violation;
    }

    public void setViolation(InferViolation violation) {
        this.violation = violation;
    }
}
