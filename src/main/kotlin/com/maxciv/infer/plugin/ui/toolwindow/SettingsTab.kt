package com.maxciv.infer.plugin.ui.toolwindow

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.ui.JBUI
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.actions.AnalysisActions
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.process.BuildTools
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
class SettingsTab(private val project: Project) : JPanel(BorderLayout()) {

    //region UI
    private val inferPathLabel = JLabel("Infer binaries path", SwingConstants.LEFT)
    private val inferPathTextField = JTextField("infer", SwingConstants.LEFT)
    private val chooseInferPathButton = JButton("Choose")

    private val inferWorkingDirLabel = JLabel("Infer working directory", SwingConstants.LEFT)
    private val inferWorkingDirTextField = JTextField("./infer-out", SwingConstants.LEFT)
    private val chooseInferWorkingDirButton = JButton("Choose")

    private val buildToolLabel = JLabel("Build tool", SwingConstants.LEFT)
    private val buildToolComboBox: ComboBox<String>

    private val compilerArgsLabel = JLabel("Compiler arguments", SwingConstants.LEFT)
    val compilerArgsTextField = JTextField("", SwingConstants.LEFT)

    private val runFullAnalysisButton = JButton("Run pre-analysis")

    private val compileOnModuleAnalysisCheckBox = JCheckBox("Compile before Module Analysis")
    //endregion

    private val pluginSettings: InferPluginSettings =
        project.getComponent(InferProjectComponent::class.java).pluginSettings

    init {
        buildToolComboBox = createBuildToolComboBox()
        compilerArgsTextField.text = pluginSettings.projectModules.joinToString(" ")

        inferPathTextField.text = pluginSettings.inferPath
        inferPathTextField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                save()
            }

            override fun removeUpdate(e: DocumentEvent) {
                save()
            }

            override fun changedUpdate(e: DocumentEvent) {
                save()
            }

            private fun save() {
                pluginSettings.inferPath = inferPathTextField.text
            }
        })

        inferWorkingDirTextField.text = pluginSettings.inferWorkingDir
        inferWorkingDirTextField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                save()
            }

            override fun removeUpdate(e: DocumentEvent) {
                save()
            }

            override fun changedUpdate(e: DocumentEvent) {
                save()
            }

            private fun save() {
                pluginSettings.inferWorkingDir = inferWorkingDirTextField.text
            }
        })

        runFullAnalysisButton.addActionListener {
            AnalysisActions.runPreAnalysis(project)
        }
        compileOnModuleAnalysisCheckBox.isSelected = pluginSettings.isCompileOnModuleAnalysisEnabled
        compileOnModuleAnalysisCheckBox.addChangeListener {
            pluginSettings.isCompileOnModuleAnalysisEnabled = compileOnModuleAnalysisCheckBox.isSelected
        }
        chooseInferPathButton.addActionListener {
            val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
            val file = FileChooser.chooseFile(descriptor, project, LocalFileSystem.getInstance().findFileByIoFile(File(pluginSettings.inferPath))) ?: return@addActionListener
            pluginSettings.inferPath = file.canonicalPath!!
            inferPathTextField.text = file.canonicalPath!!
        }
        chooseInferWorkingDirButton.addActionListener {
            val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            val file = FileChooser.chooseFile(descriptor, project, LocalFileSystem.getInstance().findFileByIoFile(File(pluginSettings.inferWorkingDir))) ?: return@addActionListener
            pluginSettings.inferWorkingDir = file.canonicalPath!!
            inferWorkingDirTextField.text = file.canonicalPath!!
        }
        add(createMainPanel(), BorderLayout.NORTH)
        project.getComponent(InferProjectComponent::class.java).settingsTab = this
    }

    private fun createMainPanel(): JPanel {
        val mainPanel = JPanel(GridBagLayout())

        mainPanel.add(
            inferPathLabel, GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            inferPathTextField, GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            chooseInferPathButton, GridBagConstraints(
                2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            buildToolLabel, GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            buildToolComboBox, GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            runFullAnalysisButton, GridBagConstraints(
                2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            compilerArgsLabel, GridBagConstraints(
                0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            compilerArgsTextField, GridBagConstraints(
                1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            compileOnModuleAnalysisCheckBox, GridBagConstraints(
                0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            inferWorkingDirLabel, GridBagConstraints(
                0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            inferWorkingDirTextField, GridBagConstraints(
                1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            chooseInferWorkingDirButton, GridBagConstraints(
                2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        return mainPanel
    }

    private fun createBuildToolComboBox(): ComboBox<String> {
        val newComboBox = ComboBox<String>()
        newComboBox.model = DefaultComboBoxModel(BUILD_TOOLS_STRINGS)
        newComboBox.selectedItem = pluginSettings.buildTool.name
        newComboBox.addActionListener { actionEvent ->
            val comboBox = actionEvent.source as ComboBox<*>
            pluginSettings.buildTool = BuildTools.valueOf(comboBox.selectedItem as String)
        }
        return newComboBox
    }

    companion object {
        private val COMPONENT_INSETS = JBUI.insets(4, 7, 4, 4)
        private val BUILD_TOOLS_STRINGS =
            BuildTools.values().filter { it != BuildTools.DEFAULT }.map { it.name }.toTypedArray()
    }
}
