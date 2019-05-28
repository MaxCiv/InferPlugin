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
import com.maxciv.infer.plugin.process.InferDownloader
import com.maxciv.infer.plugin.process.OperationSystems
import com.maxciv.infer.plugin.process.report.ReportExporter
import com.maxciv.infer.plugin.process.report.ReportImporter
import icons.InferIcons.ICON_ARRAY
import icons.InferIcons.ICON_BUILD_TOOLS
import icons.InferIcons.ICON_CONSOLE
import icons.InferIcons.ICON_DOWNLOAD
import icons.InferIcons.ICON_FULL_REPORT
import icons.InferIcons.ICON_INFER_WORKING_DIR
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.Document

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
class SettingsTab(private val project: Project) : JPanel(BorderLayout()) {

    private val inferProjectComponent: InferProjectComponent = project.getComponent(InferProjectComponent::class.java)
    private val pluginSettings: InferPluginSettings = inferProjectComponent.pluginSettings
    private val inferDownloader: InferDownloader = InferDownloader(project)

    //region UI
    private val shortClassNamesCheckBox = JCheckBox("Use short class names").apply {
        isSelected = pluginSettings.isShortClassNamesEnabled
        addChangeListener { pluginSettings.isShortClassNamesEnabled = isSelected }
    }

    private val compileOnModuleAnalysisCheckBox = JCheckBox("Compile before Module Analysis").apply {
        isSelected = pluginSettings.isCompileOnModuleAnalysisEnabled
        addChangeListener { pluginSettings.isCompileOnModuleAnalysisEnabled = isSelected }
    }

    private val compileOnlyOneModuleOnModuleAnalysisCheckBox =
        JCheckBox("Compile only current module before Module Analysis").apply {
            isSelected = pluginSettings.isCompileOnlyOneModuleOnModuleAnalysisEnabled
            addChangeListener { pluginSettings.isCompileOnlyOneModuleOnModuleAnalysisEnabled = isSelected }
        }

    private val differentDirsForModulesCheckBox = JCheckBox("Different result dirs for modules").apply {
        isSelected = pluginSettings.isDifferentDirsForModulesEnabled
        addChangeListener { pluginSettings.isDifferentDirsForModulesEnabled = isSelected }
    }

    private val buildToolLabel = JLabel("Build tool:", ICON_BUILD_TOOLS, SwingConstants.LEFT)
    private val buildToolComboBox: ComboBox<String> = createBuildToolComboBox()
    private val runFullAnalysisButton = JButton("Run pre-analysis")

    private val importExportLabel = JLabel("Report:", ICON_FULL_REPORT, SwingConstants.LEFT)
    private val importReportButton = JButton("Import")
    private val exportReportButton = JButton("Export")

    private val inferPathLabel = JLabel("Infer binaries path:", ICON_CONSOLE, SwingConstants.LEFT)
    private val inferPathTextField = JTextField(pluginSettings.inferPath, SwingConstants.LEFT)
    private val chooseInferPathButton = JButton("Choose")

    private val inferWorkingDirLabel = JLabel("Infer working directory:", ICON_INFER_WORKING_DIR, SwingConstants.LEFT)
    private val inferWorkingDirTextField = JTextField(pluginSettings.inferWorkingDir, SwingConstants.LEFT)
    private val chooseInferWorkingDirButton = JButton("Choose")

    private val downloadInferLabel = JLabel("Download Infer:", ICON_DOWNLOAD, SwingConstants.LEFT)
    private val osComboBox: ComboBox<String> = createOsComboBox()
    private val inferVersionComboBox: ComboBox<String> = createInferVersionComboBox()
    private val downloadInferButton = JButton("Download")

    private val mavenCaptureTaskLabel = JLabel("Maven capture task:", ICON_ARRAY, SwingConstants.LEFT)
    private val mavenCaptureTaskTextField = JTextField(pluginSettings.mavenCaptureTask, SwingConstants.LEFT)
    private val mavenArgsTextField =
        JTextField(pluginSettings.mavenUserArguments.joinToString(" "), SwingConstants.LEFT)
    private val mavenArgsLabel = JLabel(":Maven args", ICON_ARRAY, SwingConstants.LEFT)

    private val gradleCaptureTaskLabel = JLabel("Gradle capture task:", ICON_ARRAY, SwingConstants.LEFT)
    private val gradleCaptureTaskTextField = JTextField(pluginSettings.gradleCaptureTask, SwingConstants.LEFT)
    private val gradleArgsTextField =
        JTextField(pluginSettings.gradleUserArguments.joinToString(" "), SwingConstants.LEFT)
    private val gradleArgsLabel = JLabel(":Gradle args", ICON_ARRAY, SwingConstants.LEFT)
    //endregion

    init {
        inferPathTextField.document.addDocumentListener {
            pluginSettings.inferPath = inferPathTextField.text
        }

        inferWorkingDirTextField.document.addDocumentListener {
            pluginSettings.inferWorkingDir = inferWorkingDirTextField.text
        }

        mavenArgsTextField.document.addDocumentListener {
            pluginSettings.mavenUserArguments =
                mavenArgsTextField.text.split(" ").filter { it.isNotBlank() }.toMutableList()
        }

        gradleArgsTextField.document.addDocumentListener {
            pluginSettings.gradleUserArguments =
                gradleArgsTextField.text.split(" ").filter { it.isNotBlank() }.toMutableList()
        }

        mavenCaptureTaskTextField.document.addDocumentListener {
            pluginSettings.mavenCaptureTask = mavenCaptureTaskTextField.text
        }

        gradleCaptureTaskTextField.document.addDocumentListener {
            pluginSettings.gradleCaptureTask = gradleCaptureTaskTextField.text
        }

        importReportButton.addActionListener {
            ReportImporter.importReport(project, pluginSettings)
            inferProjectComponent.resultsTab.updateFullReportTree()
            inferProjectComponent.resultsTab.updateCurrentFileTree()
        }
        exportReportButton.addActionListener {
            ReportExporter.exportReport(project.basePath!!, pluginSettings.aggregatedInferReport)
        }

        runFullAnalysisButton.addActionListener {
            AnalysisActions.runPreAnalysis(project)
        }
        chooseInferPathButton.addActionListener {
            val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
            val file = FileChooser.chooseFile(
                descriptor,
                project,
                LocalFileSystem.getInstance().findFileByIoFile(File(pluginSettings.inferPath))
            ) ?: return@addActionListener
            pluginSettings.inferPath = file.canonicalPath!!
            inferPathTextField.text = file.canonicalPath!!
        }
        chooseInferWorkingDirButton.addActionListener {
            val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            val file = FileChooser.chooseFile(
                descriptor,
                project,
                LocalFileSystem.getInstance().findFileByIoFile(File(pluginSettings.inferWorkingDir))
            ) ?: return@addActionListener
            pluginSettings.inferWorkingDir = file.canonicalPath!!
            inferWorkingDirTextField.text = file.canonicalPath!!
        }
        downloadInferButton.addActionListener {
            inferDownloader.downloadAndInstall(
                inferVersionComboBox.selectedItem as String,
                OperationSystems.valueOfTitle(osComboBox.selectedItem as String)
            )
        }
        updateBuildToolFieldsVisibility()
        add(createMainPanel(), BorderLayout.NORTH)
        project.getComponent(InferProjectComponent::class.java).settingsTab = this
    }

    private fun createMainPanel(): JPanel = JPanel(GridBagLayout()).apply {
        add(
            compileOnModuleAnalysisCheckBox, GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            compileOnlyOneModuleOnModuleAnalysisCheckBox, GridBagConstraints(
                1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            differentDirsForModulesCheckBox, GridBagConstraints(
                2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            shortClassNamesCheckBox, GridBagConstraints(
                3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            buildToolLabel, GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            buildToolComboBox, GridBagConstraints(
                1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            runFullAnalysisButton, GridBagConstraints(
                3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            importExportLabel, GridBagConstraints(
                0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            importReportButton, GridBagConstraints(
                1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            exportReportButton, GridBagConstraints(
                2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            inferPathLabel, GridBagConstraints(
                0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            inferPathTextField, GridBagConstraints(
                1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            chooseInferPathButton, GridBagConstraints(
                3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            inferWorkingDirLabel, GridBagConstraints(
                0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            inferWorkingDirTextField, GridBagConstraints(
                1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            chooseInferWorkingDirButton, GridBagConstraints(
                3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            downloadInferLabel, GridBagConstraints(
                0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            osComboBox, GridBagConstraints(
                1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            inferVersionComboBox, GridBagConstraints(
                2, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            downloadInferButton, GridBagConstraints(
                3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            mavenCaptureTaskLabel, GridBagConstraints(
                0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            mavenCaptureTaskTextField, GridBagConstraints(
                1, 7, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            mavenArgsTextField, GridBagConstraints(
                2, 7, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            mavenArgsLabel, GridBagConstraints(
                3, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        //
        add(
            gradleCaptureTaskLabel, GridBagConstraints(
                0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            gradleCaptureTaskTextField, GridBagConstraints(
                1, 8, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            gradleArgsTextField, GridBagConstraints(
                2, 8, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        add(
            gradleArgsLabel, GridBagConstraints(
                3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
    }

    fun updateInferBinPath(path: String) {
        pluginSettings.inferPath = path
        inferPathTextField.text = path
    }

    private fun createBuildToolComboBox(): ComboBox<String> = ComboBox<String>().apply {
        model = DefaultComboBoxModel(BUILD_TOOLS_STRINGS)
        selectedItem = pluginSettings.buildTool.name
        addActionListener { actionEvent ->
            val comboBox = actionEvent.source as ComboBox<*>
            pluginSettings.buildTool = BuildTools.valueOf(comboBox.selectedItem as String)
            updateBuildToolFieldsVisibility()
        }
    }

    private fun createOsComboBox(): ComboBox<String> = ComboBox<String>().apply {
        model = DefaultComboBoxModel(OS_STRINGS)
        selectedItem = pluginSettings.os.title
        addActionListener { actionEvent ->
            val comboBox = actionEvent.source as ComboBox<*>
            pluginSettings.os = OperationSystems.valueOfTitle(comboBox.selectedItem as String)
        }
    }

    private fun createInferVersionComboBox(): ComboBox<String> = ComboBox<String>().apply {
        model = DefaultComboBoxModel(OS_STRINGS)
        GlobalScope.launch {
            model = DefaultComboBoxModel(inferDownloader.getVersionList().toTypedArray())
        }
    }

    private fun updateBuildToolFieldsVisibility() {
        when (pluginSettings.buildTool) {
            BuildTools.MAVEN, BuildTools.MAVENW -> {
                mavenCaptureTaskLabel.isVisible = true
                mavenCaptureTaskTextField.isVisible = true
                mavenArgsTextField.isVisible = true
                mavenArgsLabel.isVisible = true
                gradleCaptureTaskLabel.isVisible = false
                gradleCaptureTaskTextField.isVisible = false
                gradleArgsTextField.isVisible = false
                gradleArgsLabel.isVisible = false
            }
            BuildTools.GRADLE, BuildTools.GRADLEW -> {
                mavenCaptureTaskLabel.isVisible = false
                mavenCaptureTaskTextField.isVisible = false
                mavenArgsTextField.isVisible = false
                mavenArgsLabel.isVisible = false
                gradleCaptureTaskLabel.isVisible = true
                gradleCaptureTaskTextField.isVisible = true
                gradleArgsTextField.isVisible = true
                gradleArgsLabel.isVisible = true
            }
        }
    }

    private inline fun Document.addDocumentListener(crossinline block: () -> Unit) {
        this.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = block()
            override fun removeUpdate(e: DocumentEvent) = block()
            override fun changedUpdate(e: DocumentEvent) = block()
        })
    }

    companion object {
        private val COMPONENT_INSETS = JBUI.insets(4, 7, 4, 4)
        private val BUILD_TOOLS_STRINGS =
            BuildTools.values().filter { it != BuildTools.DEFAULT }.map { it.name }.toTypedArray()
        private val OS_STRINGS =
            OperationSystems.values().filter { it != OperationSystems.DEFAULT }.map { it.title }.toTypedArray()
    }
}
