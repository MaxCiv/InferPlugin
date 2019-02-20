package com.maxciv.infer.plugin.toolwindow

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.IconLoader
import com.intellij.util.ui.JBUI
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.config.InferPluginSettings
import com.maxciv.infer.plugin.process.BuildTools
import com.maxciv.infer.plugin.process.InferRunner
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
class SettingsTab(private val project: Project) : JPanel(BorderLayout()) {

    private val inferPathLabel = JLabel("Infer binaries path", SwingConstants.LEFT)
    private val inferPathTextField = JTextField("infer", SwingConstants.LEFT)

    private val buildToolLabel = JLabel("Build tool", SwingConstants.LEFT)
    private val buildToolComboBox: ComboBox<String>

    private val runButton = JButton("Run analysis")
    private val pluginSettings: InferPluginSettings? = project.getComponent(InferProjectComponent::class.java).pluginSettings

    init {
        this.buildToolComboBox = createBuildToolComboBox()
        this.inferPathTextField.text = pluginSettings!!.inferPath
        this.inferPathTextField.document.addDocumentListener(object : DocumentListener {
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

        this.runButton.addActionListener { event ->
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
                override fun run(indicator: ProgressIndicator) {
                    indicator.isIndeterminate = true

                    try {
                        project.getComponent(InferProjectComponent::class.java).resultsTab!!.fillTreeFromResult(
                                InferRunner(project.basePath!!, pluginSettings!!.inferPath).runAnalysis(pluginSettings.buildTool)!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
        }

        add(createMainPanel(), BorderLayout.NORTH)
        project.getComponent(InferProjectComponent::class.java).settingsTab = this
    }

    private fun createMainPanel(): JPanel {
        val mainPanel = JPanel(GridBagLayout())

        mainPanel.add(inferPathLabel, GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0))
        mainPanel.add(inferPathTextField, GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0))

        mainPanel.add(buildToolLabel, GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0))
        mainPanel.add(buildToolComboBox, GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0))

        mainPanel.add(runButton, GridBagConstraints(
                0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0))

        return mainPanel
    }

    private fun createBuildToolComboBox(): ComboBox<String> {
        val newComboBox = ComboBox<String>()
        newComboBox.model = DefaultComboBoxModel(BUILD_TOOLS_STRINGS)
        newComboBox.selectedItem = pluginSettings!!.buildTool.name
        newComboBox.addActionListener { actionEvent ->
            val comboBox = actionEvent.source as ComboBox<*>
            pluginSettings!!.buildTool = BuildTools.valueOf(comboBox.selectedItem as String)
        }
        return newComboBox
    }

    companion object {
        private val ICON = IconLoader.getIcon("/icons/testPassed.png")
        private val COMPONENT_INSETS = JBUI.insets(4, 7, 4, 4)

        private val BUILD_TOOLS_STRINGS = arrayOf(BuildTools.MAVEN.name, BuildTools.GRADLE.name)
    }
}
