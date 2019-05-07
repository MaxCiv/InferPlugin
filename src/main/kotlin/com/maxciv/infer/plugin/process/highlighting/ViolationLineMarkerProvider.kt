package com.maxciv.infer.plugin.process.highlighting

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.maxciv.infer.plugin.InferProjectComponent
import com.maxciv.infer.plugin.process.highlighting.GutterLineMarkerProducer.createLineMarkerInfo

/**
 * Выставляет иконку с текстом ошибки рядом с номером строки
 *
 * @author maxim.oleynik
 * @since 06.05.2019
 */
class ViolationLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element is LeafPsiElement) {
            val inferReport = element.project.getComponent(InferProjectComponent::class.java).pluginSettings.aggregatedInferReport
            val fileWithViolation = inferReport.violationsByFile.keys
                .firstOrNull { element.containingFile.virtualFile.canonicalPath!!.contains(it) } ?: return null

            inferReport.violationsByFile[fileWithViolation].orEmpty().forEach { violation ->
                if (element.textRange.contains(violation.offset))
                    return createLineMarkerInfo(element, violation)
            }
        }
        return null
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<PsiElement>,
        result: MutableCollection<LineMarkerInfo<PsiElement>>
    ) {
    }
}
