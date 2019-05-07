package com.maxciv.infer.plugin.process.highlighting

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.maxciv.infer.plugin.data.report.InferViolation
import com.maxciv.infer.plugin.ui.InferIcons

/**
 * @author maxim.oleynik
 * @since 06.05.2019
 */
object GutterLineMarkerProducer {

    fun createLineMarkerInfo(element: PsiElement, violation: InferViolation): LineMarkerInfo<PsiElement> {
        return NavigationGutterIconBuilder.create(InferIcons.ICON_VIOLATION)
            .setTarget(element)
            .setTooltipText(violation.bugTypeHum + " – " + violation.qualifier)
            .createLineMarkerInfo(element)
    }
}
