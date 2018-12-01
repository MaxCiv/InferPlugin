package com.maxciv.infer.plugin.toolwindow;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.JBUI;
import com.maxciv.infer.plugin.InferProjectComponent;
import com.maxciv.infer.plugin.config.InferPluginSettings;
import com.maxciv.infer.plugin.process.BuildTools;
import com.maxciv.infer.plugin.process.InferRunner;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
public class SettingsTab extends JPanel {

    private static final Icon ICON = IconLoader.getIcon("/icons/testPassed.png");
    private static final Insets COMPONENT_INSETS = JBUI.insets(4, 7, 4, 4);

    private static final String[] BUILD_TOOLS_STRINGS = {BuildTools.MAVEN.name(), BuildTools.GRADLE.name()};

    private JLabel inferPathLabel = new JLabel("Infer binaries path", SwingConstants.LEFT);
    private JTextField inferPathTextField = new JTextField("infer", SwingConstants.LEFT);

    private JLabel buildToolLabel = new JLabel("Build tool", SwingConstants.LEFT);
    private ComboBox<String> buildToolComboBox;

    private JButton runButton = new JButton("Run analysis");

    private Project project;
    private InferPluginSettings pluginSettings;

    public SettingsTab(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.pluginSettings = project.getComponent(InferProjectComponent.class).getPluginSettings();
        this.buildToolComboBox = createBuildToolComboBox();
        this.inferPathTextField.setText(pluginSettings.getInferPath());
        this.inferPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                save();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                save();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                save();
            }

            private void save() {
                pluginSettings.setInferPath(inferPathTextField.getText());
            }
        });

        this.runButton.addActionListener(event -> ProgressManager.getInstance().run(new Task.Backgroundable(project, "Infer Running...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                try {
                    project.getComponent(InferProjectComponent.class).getResultsTab().fillTreeFromResult(
                            new InferRunner(project.getBasePath(), pluginSettings.getInferPath()).runAnalysis(pluginSettings.getBuildTool()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        add(createMainPanel(), BorderLayout.NORTH);
        project.getComponent(InferProjectComponent.class).setSettingsTab(this);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());

        mainPanel.add(inferPathLabel, new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0));
        mainPanel.add(inferPathTextField, new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0));

        mainPanel.add(buildToolLabel, new GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0));
        mainPanel.add(buildToolComboBox, new GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0));

        mainPanel.add(runButton, new GridBagConstraints(
                0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0));

        return mainPanel;
    }

    private ComboBox<String> createBuildToolComboBox() {
        ComboBox<String> newComboBox = new ComboBox<>();
        newComboBox.setModel(new DefaultComboBoxModel<>(BUILD_TOOLS_STRINGS));
        newComboBox.setSelectedItem(pluginSettings.getBuildTool().name());
        newComboBox.addActionListener((actionEvent) -> {
            ComboBox comboBox = (ComboBox) actionEvent.getSource();
            pluginSettings.setBuildTool(BuildTools.valueOf((String) comboBox.getSelectedItem()));
        });
        return newComboBox;
    }
}
