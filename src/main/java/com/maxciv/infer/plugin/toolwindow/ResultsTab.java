package com.maxciv.infer.plugin.toolwindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.maxciv.infer.plugin.InferProjectComponent;
import com.maxciv.infer.plugin.config.InferPluginSettings;
import com.maxciv.infer.plugin.process.report.InferReport;
import com.maxciv.infer.plugin.tree.CellRenderer;
import com.maxciv.infer.plugin.tree.RootNode;
import com.maxciv.infer.plugin.tree.TreeNodeFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * @author maxim.oleynik
 * @since 01.12.2018
 */
public class ResultsTab extends JPanel {

    private static final TreeNodeFactory TREE_NODE_FACTORY = TreeNodeFactory.getInstance();

    private Project project;
    private InferPluginSettings pluginSettings;
    private Tree treeResults;
    private RootNode rootNode;

    public ResultsTab(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.pluginSettings = project.getComponent(InferProjectComponent.class).getPluginSettings();

        this.treeResults = createNewTree();
        add(new JBScrollPane(treeResults), BorderLayout.CENTER);

        project.getComponent(InferProjectComponent.class).setResultsTab(this);
    }

    private Tree createNewTree() {
        Tree newTree = new Tree();
        rootNode = (RootNode) TREE_NODE_FACTORY.createDefaultRootNode();
        TreeModel treeModel = new DefaultTreeModel(rootNode);
        newTree.setModel(treeModel);
        newTree.setCellRenderer(new CellRenderer());
        return newTree;
    }

    public void fillTreeFromResult(InferReport inferReport) {
        rootNode.removeAllChildren();
        rootNode.setInferReport(inferReport);
        if (!inferReport.getViolations().isEmpty()) {
            inferReport.getViolations().forEach(violation -> addNode(TREE_NODE_FACTORY.createNode(violation)));
        } else {
            addNode(TREE_NODE_FACTORY.createNode("No violations found."));
        }
    }

    private DefaultMutableTreeNode addNode(DefaultMutableTreeNode node) {
        return addNode(rootNode, node);
    }

    private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode node) {
        parent.add(node);
        ApplicationManager.getApplication().invokeLater(() -> ((DefaultTreeModel) treeResults.getModel()).reload());
        return node;
    }
}
