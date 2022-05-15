package com.beetlestance.paper.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.SpanStyle

@Composable
fun PaperSpanToggle(
    value: PaperEditorValue,
    onValueChange: (PaperEditorValue) -> Unit,
    spanFactory: () -> SpanStyle,
    spanEqualPredicate: (SpanStyle) -> Boolean,
    content: @Composable (
        enabled: Boolean,
        onToggle: () -> Unit
    ) -> Unit
) {
    val enabled = remember(value) {
        if (value.selection.collapsed) {
            value.paperString.spanStyles.any { range ->
                spanEqualPredicate(range.item) && shouldExpandSpanOnTextAddition(range, value.selection.start)
            }
        } else {
            value.paperString.spanStyles.fillsRange(
                start = value.selection.min,
                end = value.selection.max,
                predicate = spanEqualPredicate
            )
        }
    }

    content(
        enabled
    ) {
        val selection = value.selection

        if (!enabled) {
            onValueChange(
                value.plusSpanStyle(
                    PaperString.Range(
                        item = spanFactory(),
                        start = selection.min, end = selection.max,
                        startInclusive = false, endInclusive = true
                    )
                )
            )
        } else {
            onValueChange(
                value.copy(
                    paperString = value.paperString.copy(
                        spanStyles = value.paperString.spanStyles.minusSpansInRange(
                            start = selection.min,
                            endExclusive = selection.max,
                            predicate = spanEqualPredicate
                        )
                    )
                )
            )
        }
    }
}
