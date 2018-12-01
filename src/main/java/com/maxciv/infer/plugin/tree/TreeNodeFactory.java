package com.maxciv.infer.plugin.tree;

import com.maxciv.infer.plugin.process.report.InferReport;
import com.maxciv.infer.plugin.process.report.InferViolation;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public final class TreeNodeFactory {

    private static TreeNodeFactory instance = new TreeNodeFactory();

    private TreeNodeFactory() {
    }

    public static TreeNodeFactory getInstance() {
        return instance;
    }

    public DefaultMutableTreeNode createNode(Object userObject) {
        if (userObject instanceof InferViolation) {
            return new ViolationNode((InferViolation) userObject);
        } else if (userObject instanceof InferReport) {
            return new RootNode((InferReport) userObject);
        } else if (userObject instanceof String) {
            return new StringNode((String) userObject);
        }
        return null;
    }

    public DefaultMutableTreeNode createNode(InferViolation violation) {
        return new ViolationNode(violation);
    }

    public DefaultMutableTreeNode createDefaultRootNode() {
        return new RootNode();
    }
}
