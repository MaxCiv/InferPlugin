package icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * @author maxim.oleynik
 * @since 06.05.2019
 */
object InferIcons {
    val ICON_VIOLATION: Icon = AllIcons.General.BalloonError
    val ICON_REPORT_CURRENT_FILE: Icon = AllIcons.FileTypes.Properties
    val ICON_FULL_REPORT: Icon = AllIcons.Nodes.ResourceBundle
    val ICON_CLASS: Icon = AllIcons.Nodes.Class
    val ICON_INFER: Icon = IconLoader.getIcon("icons/infer.png")
    val ICON_INFER_WORKING_DIR: Icon = AllIcons.Modules.ExcludeRoot
    val ICON_BUILD_TOOLS: Icon = AllIcons.General.ExternalTools
    val ICON_DARK_BUILD: Icon = AllIcons.Toolwindows.ToolWindowBuild
    val ICON_GREEN_BUILD: Icon = AllIcons.Actions.Compile
    val ICON_ABSTRACT_CLASS: Icon = AllIcons.Nodes.AbstractClass
    val ICON_DOWNLOAD: Icon = AllIcons.Actions.Download
}
