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
import icons.InferIcons.ICON_BUILD_TOOLS
import icons.InferIcons.ICON_DOWNLOAD
import icons.InferIcons.ICON_FULL_REPORT
import icons.InferIcons.ICON_INFER
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

/**
 * @author maxim.oleynik
 * @since 28.11.2018
 */
class SettingsTab(private val project: Project) : JPanel(BorderLayout()) {

    //region UI
    private val shortClassNamesCheckBox = JCheckBox("Use short class names")
    private val compileOnModuleAnalysisCheckBox = JCheckBox("Compile before Module Analysis")
    private val compileOnlyOneModuleOnModuleAnalysisCheckBox =
        JCheckBox("Compile only current module before Module Analysis")

    private val buildToolLabel = JLabel("Build tool:", ICON_BUILD_TOOLS, SwingConstants.LEFT)
    private val buildToolComboBox: ComboBox<String>
    private val runFullAnalysisButton = JButton("Run pre-analysis")

    private val importExportLabel = JLabel("Report:", ICON_FULL_REPORT, SwingConstants.LEFT)
    private val importReportButton = JButton("Import")
    private val exportReportButton = JButton("Export")

    private val inferPathLabel = JLabel("Infer binaries path:", ICON_INFER, SwingConstants.LEFT)
    private val inferPathTextField = JTextField("infer", SwingConstants.LEFT)
    private val chooseInferPathButton = JButton("Choose")

    private val inferWorkingDirLabel = JLabel("Infer working directory:", ICON_INFER_WORKING_DIR, SwingConstants.LEFT)
    private val inferWorkingDirTextField = JTextField("./infer-out", SwingConstants.LEFT)
    private val chooseInferWorkingDirButton = JButton("Choose")

    private val downloadInferLabel = JLabel("Download Infer:", ICON_DOWNLOAD, SwingConstants.LEFT)
    private val osComboBox: ComboBox<String>
    private val inferVersionComboBox: ComboBox<String>
    private val downloadInferButton = JButton("Download")

    private val compilerArgsLabel = JLabel("Compiler arguments", SwingConstants.LEFT)
    val compilerArgsTextField = JTextField("", SwingConstants.LEFT)
    //endregion

    private val inferProjectComponent: InferProjectComponent = project.getComponent(InferProjectComponent::class.java)
    private val pluginSettings: InferPluginSettings = inferProjectComponent.pluginSettings
    private val inferDownloader: InferDownloader = InferDownloader(project)

    init {
        buildToolComboBox = createBuildToolComboBox()
        osComboBox = createOsComboBox()
        inferVersionComboBox = createInferVersionComboBox()

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
        shortClassNamesCheckBox.isSelected = pluginSettings.isShortClassNamesEnabled
        shortClassNamesCheckBox.addChangeListener {
            pluginSettings.isShortClassNamesEnabled = shortClassNamesCheckBox.isSelected
        }
        compileOnModuleAnalysisCheckBox.isSelected = pluginSettings.isCompileOnModuleAnalysisEnabled
        compileOnModuleAnalysisCheckBox.addChangeListener {
            pluginSettings.isCompileOnModuleAnalysisEnabled = compileOnModuleAnalysisCheckBox.isSelected
        }
        compileOnlyOneModuleOnModuleAnalysisCheckBox.isSelected =
            pluginSettings.isCompileOnlyOneModuleOnModuleAnalysisEnabled
        compileOnlyOneModuleOnModuleAnalysisCheckBox.addChangeListener {
            pluginSettings.isCompileOnlyOneModuleOnModuleAnalysisEnabled =
                compileOnlyOneModuleOnModuleAnalysisCheckBox.isSelected
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
        add(createMainPanel(), BorderLayout.NORTH)
        project.getComponent(InferProjectComponent::class.java).settingsTab = this
    }

    private fun createMainPanel(): JPanel {
        val mainPanel = JPanel(GridBagLayout())

        mainPanel.add(
            compileOnModuleAnalysisCheckBox, GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            compileOnlyOneModuleOnModuleAnalysisCheckBox, GridBagConstraints(
                1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            shortClassNamesCheckBox, GridBagConstraints(
                3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
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
                1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            runFullAnalysisButton, GridBagConstraints(
                3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            importExportLabel, GridBagConstraints(
                0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            importReportButton, GridBagConstraints(
                1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            exportReportButton, GridBagConstraints(
                2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            inferPathLabel, GridBagConstraints(
                0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            inferPathTextField, GridBagConstraints(
                1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            chooseInferPathButton, GridBagConstraints(
                3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
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
                1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            chooseInferWorkingDirButton, GridBagConstraints(
                3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

        mainPanel.add(
            downloadInferLabel, GridBagConstraints(
                0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            osComboBox, GridBagConstraints(
                1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            inferVersionComboBox, GridBagConstraints(
                2, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )
        mainPanel.add(
            downloadInferButton, GridBagConstraints(
                3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
            )
        )

//        mainPanel.add(
//            compilerArgsLabel, GridBagConstraints(
//                0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//                GridBagConstraints.NONE, COMPONENT_INSETS, 0, 0
//            )
//        )
//        mainPanel.add(
//            compilerArgsTextField, GridBagConstraints(
//                1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
//                GridBagConstraints.HORIZONTAL, COMPONENT_INSETS, 0, 0
//            )
//        )

        return mainPanel
    }

    fun updateInferBinPath(path: String) {
        pluginSettings.inferPath = path
        inferPathTextField.text = path
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

    private fun createOsComboBox(): ComboBox<String> {
        val newComboBox = ComboBox<String>()
        newComboBox.model = DefaultComboBoxModel(OS_STRINGS)
        newComboBox.selectedItem = pluginSettings.os.title
        newComboBox.addActionListener { actionEvent ->
            val comboBox = actionEvent.source as ComboBox<*>
            pluginSettings.os = OperationSystems.valueOfTitle(comboBox.selectedItem as String)
        }
        return newComboBox
    }

    private fun createInferVersionComboBox(): ComboBox<String> {
        val newComboBox = ComboBox<String>()
        newComboBox.model = DefaultComboBoxModel(OS_STRINGS)
        GlobalScope.launch {
            newComboBox.model = DefaultComboBoxModel(inferDownloader.getVersionList().toTypedArray())
        }
        return newComboBox
    }

    companion object {
        private val COMPONENT_INSETS = JBUI.insets(4, 7, 4, 4)
        private val BUILD_TOOLS_STRINGS =
            BuildTools.values().filter { it != BuildTools.DEFAULT }.map { it.name }.toTypedArray()
        private val OS_STRINGS =
            OperationSystems.values().filter { it != OperationSystems.DEFAULT }.map { it.title }.toTypedArray()
    }
}
