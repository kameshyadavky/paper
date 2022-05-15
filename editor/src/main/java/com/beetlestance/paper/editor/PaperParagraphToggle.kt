package com.beetlestance.paper.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.ParagraphStyle

@Composable
fun PaperParagraphToggle(
    value: PaperEditorValue,
    onValueChange: (PaperEditorValue) -> Unit,
    paragraphFactory: () -> ParagraphStyle,
    paragraphEqualPredicate: (ParagraphStyle) -> Boolean,
    content: @Composable (
        enabled: Boolean,
        onToggle: () -> Unit
    ) -> Unit
) {
    val enabled = remember(value) {
        if (value.selection.collapsed) {
            value.paperString.paragraphStyles.any { range ->
                paragraphEqualPredicate(range.item) && shouldExpandSpanOnTextAddition(
                    range,
                    value.selection.start
                )
            }
        } else {
            value.paperString.paragraphStyles.fillsRange(
                start = value.selection.min,
                end = value.selection.max,
                predicate = paragraphEqualPredicate
            )
        }
    }

    content(
        enabled
    ) {
        val selection = value.selection

        if (!enabled) {
            onValueChange(
                value.plusParagraphStyle(
                    PaperString.Range(
                        item = paragraphFactory(),
                        start = selection.min, end = selection.max,
                        startInclusive = true, endInclusive = true
                    )
                )
            )
        } else {
            onValueChange(
                value.copy(
                    paperString = value.paperString.copy(
                        paragraphStyles = value.paperString.paragraphStyles.removeIntersectingWithRange(
                            start = selection.min,
                            end = selection.max,
                            predicate = paragraphEqualPredicate
                        )
                    )
                )
            )
        }
    }
}
