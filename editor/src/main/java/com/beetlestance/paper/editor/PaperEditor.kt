package com.beetlestance.paper.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.math.min

/**
 * Create a composable to hold any type of composable we will convert the [PaperEditorValue] to
 * return TextFieldValue
 * Developer can choose to use any type of text field
 */
@Composable
fun PaperEditor(
    value: PaperEditorValue,
    onValueChange: (PaperEditorValue) -> Unit,
    content: @Composable (
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit
    ) -> Unit
) {
    val textFieldValue = remember(value) { value.toTextFieldValue() }

    content(
        textFieldValue
    ) { newValue ->
        // this block is called when text field value changes

        // checks if the exact text is changed between a start and end
        val textChangedInRange: (Int, Int) -> Boolean = { start, end ->
            assert(value.paperString.text.length == newValue.text.length) {
                "the lambda should be called only if old and new length is the same"
            }

            !value.paperString.text.equalsInRange(newValue.text, start, end)
        }

        val textLengthDelta = newValue.text.length - value.paperString.text.length
        val newSpanStyles = value.paperString.spanStyles.offsetSpansAccordingToSelectionChange(
            textLengthDelta, textChangedInRange,
            value.selection, newValue.selection, SpanOnDeleteStart
        )

        val newParagraphStyles =
            value.paperString.paragraphStyles.offsetSpansAccordingToSelectionChange(
                textLengthDelta, textChangedInRange,
                value.selection, newValue.selection, ParagraphOnDeleteStart
            )

        if (newSpanStyles == null && newParagraphStyles == null) {
            onValueChange(
                value.copy(
                    selection = newValue.selection,
                    composition = newValue.composition
                )
            )
        } else {
            onValueChange(
                PaperEditorValue(
                    paperString = PaperString(
                        text = newValue.text,
                        spanStyles = newSpanStyles ?: value.paperString.spanStyles,
                        paragraphStyles = newParagraphStyles
                            ?: value.paperString.paragraphStyles
                    ),
                    selection = newValue.selection,
                    composition = newValue.composition
                )
            )
        }
    }
}

/**
 * Check if the string is exactly equal for the range
 */
private fun String.equalsInRange(other: String, start: Int, end: Int): Boolean {
    for (i in start until end) {
        if (this[i] != other[i]) return false
    }

    return true
}

// selection is removing an empty span
// only remove whole span in case it is empty
// e.g.) "abc []|def"  (let '[]' be an empty span and '|' be a cursor)
//   ~~> "abc|def"
internal val SpanOnDeleteStart: (start: Int, end: Int) -> Boolean = { start, end ->
    start == end
}

// whole paragraph span should be removed if the start of the span is deleted
// e.g.) "abc[|paragraph]"  (let '[...]' be a paragraph span and '|' be a cursor)
//   ~~> "abcparagraph"
internal val ParagraphOnDeleteStart: (start: Int, end: Int) -> Boolean = { _, _ ->
    true
}

/**
 * Move spans according to text edits. Returns `null` if only a selection has been changed and
 * span ranges remain unchanged.
 *
 * @param textChangedInRange There is a conflict between pasting and selection change where
 *  oldSelection.max == newSelection.min. To mitigate this issue, we infer if only selection has
 *  changed or text has changed by comparing the string values. Although this method has a limitation
 *  where it cannot distinguish if pasted text is the same as the original one, it is not a common
 *  case? Note that the lambda is only called when the old and new length are the same.
 * @param onDeleteStart If deleting a start of the span, the whole span is removed if the lambda
 *  returns `true`. This is needed to switch a strategy between span styles and paragraph styles.
 *  A span styles should be removed only if the range is empty, while a paragraph style should be
 *  removed immediately when the start of the span is deleted.
 */
internal fun <T> List<PaperString.Range<T>>.offsetSpansAccordingToSelectionChange(
    textLengthDelta: Int,
    textChangedInRange: (start: Int, end: Int) -> Boolean,
    oldSelection: TextRange,
    newSelection: TextRange,
    onDeleteStart: (start: Int, end: Int) -> Boolean
): List<PaperString.Range<T>>? {
    val hasTextChanged =
        hasTextChanged(textLengthDelta, textChangedInRange, oldSelection, newSelection)

    return if (!hasTextChanged) {
        null
    } else {
        val addStart = oldSelection.min
        val addEnd = newSelection.max
        val addLength = addEnd - addStart

        val selMin = min(addStart, addEnd)
        val selMax = maxOf(addStart, addEnd, oldSelection.max)

        val removedLength = if (oldSelection.collapsed) {
            oldSelection.min - newSelection.min
        } else {
            oldSelection.length
        }

        val rmStart = if (oldSelection.collapsed) {
            newSelection.min
        } else {
            oldSelection.min
        }

        mapNotNull { range ->
            if (range.end < selMin) {
                range
            } else if (selMax < range.start) {
                val offset =
                    (if (addLength > 0) addLength else 0) - (if (removedLength > 0) removedLength else 0)

                range.copy(
                    start = range.start + offset,
                    end = range.end + offset
                )
            } else {
                var start = range.start

                var spanLength = range.length -
                        intersectLength(oldSelection.min, oldSelection.max, range.start, range.end)

                if (oldSelection.collapsed) {
                    if (removedLength > 0 && range.start < oldSelection.max && selMin < range.end) {
                        spanLength -= removedLength
                    }
                }

                if (removedLength > 0) {
                    if (rmStart < range.start && range.start <= rmStart + removedLength) {
                        if (onDeleteStart(range.start, range.end)) return@mapNotNull null
                    }

                    if (selMin < range.start) {
                        start = selMin
                    }
                }

                if (addLength > 0) {
                    if (shouldExpandSpanOnTextAddition(range, oldSelection.min)) {
                        spanLength += addLength
                    } else if (
                    // We should shift the end off set to the right if:
                    // 1) a text is being added in front of the span or
                    // 2) if text is being inserted at the start of the span, then we should consider if it is start inclusive
                        addStart < range.start
                        || (addStart == range.start && !range.startInclusive)
                    ) {
                        start += addLength
                    }
                }

                if (spanLength < 0) {
                    null
                } else {
                    if (range.start == start && range.length == spanLength) {
                        range
                    } else if (start < range.start && range.length > 0 && spanLength == 0) {
                        // ORIGINAL: "ab{c[def]}g" --> [] = span, {} = cursor selection
                        // NEW     : "abg"
                        null
                    } else {
                        // if the span is deleted, then make it end inclusive
                        // ORIGINAL: "abc(def)|ghi" --> () = exclusive/exclusive span
                        // NEW     : "abc(de]|ghi"     --> (] = exclusive/inclusive span
                        val endInclusive =
                            removedLength > 0 && oldSelection.max == range.end && selMin > range.start

                        range.copy(
                            start = start,
                            end = start + spanLength,
                            endInclusive = range.endInclusive || endInclusive
                        )
                    }
                }
            }
        }
    }
}

/**
 * An intersecting length between [[lStart], [lEnd]) and [[rStart], [rEnd]).
 */
internal fun intersectLength(
    lStart: Int, lEnd: Int,
    rStart: Int, rEnd: Int
): Int {
    if (rStart in lStart until lEnd) {
        return min(rEnd, lEnd) - rStart
    }

    if (lStart in rStart until rEnd) {
        return min(rEnd, lEnd) - lStart
    }

    return 0
}

/**
 * checks if a text has changed by inspecting [textLengthDelta], [oldSelection] and [newSelection].
 */
internal fun hasTextChanged(
    textLengthDelta: Int,
    textChangedInRange: (start: Int, end: Int) -> Boolean,
    oldSelection: TextRange,
    newSelection: TextRange
): Boolean {
    // case 0: if the new selection is expanded, there can not be any change in text
    if (!newSelection.collapsed) return false

    // case 1: text changed -- text length is not the same
    if (textLengthDelta != 0) return true

    // case 2: replaced -- texts in [oldSelection] is removed and added by newSelection.max - oldSelection.min
    //   this case also covers batch deletion when newSelection.max - oldSelection.min == 0.

    // e.g.)
    // ORIGINAL: "foo bar baz"
    // OLD     :     ____
    // NEW     :           |
    // REPLACED: "foohellowbaz"
    //  IMPLIES: ------------
    // ADDED   :     <---->
    // REMOVED :     <-->
    if (-oldSelection.length + newSelection.max - oldSelection.min == textLengthDelta
        && textChangedInRange(oldSelection.min, newSelection.max)
    ) {
        return true
    }

    return false
}

/**
 * Returns `true` if the [range] should increase its length if a text is added at the [cursor].
 *
 * For example, consider the case where range = (3, 5].
 * 1) If cursor was at 3 and inserted text, then the span should not increase its length, but
 *   only shift to the right because it is NOT start inclusive.
 * 2) If cursor was at 4 and inserted text, then the span should increase its length, because
 *   the text was inserted in the middle of the span.
 * 3) If cursor was at 5 and inserted text, then the span should increase its length, because
 *   the span is end inclusive.
 *
 * The behavior is slightly different from that of [PaperString.Range.contains], for
 * example, range=[3, 3), cursor=3, the range is empty but the given input returns true
 * because as it is startInclusive therefore a span should expand on text insertion.
 */
internal fun shouldExpandSpanOnTextAddition(
    range: PaperString.Range<*>,
    cursor: Int
): Boolean {
    return (range.start < cursor && cursor < range.end)
            || (range.startInclusive && range.start == cursor)
            || (range.endInclusive && range.end == cursor)
}
