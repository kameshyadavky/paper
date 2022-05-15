package com.beetlestance.paper.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Immutable
class PaperEditorValue(
    val paperString: PaperString,
    val selection: TextRange = TextRange.Zero,
    val composition: TextRange? = null
) {
    constructor(
        text: String = "",
        selection: TextRange = TextRange.Zero,
        composition: TextRange? = null
    ) : this(PaperString(text), selection, composition)

    fun copy(
        paperString: PaperString = this.paperString,
        selection: TextRange = this.selection,
        composition: TextRange? = this.composition
    ): PaperEditorValue = PaperEditorValue(
        paperString = paperString,
        selection = selection,
        composition = composition
    )
}

fun PaperEditorValue.toTextFieldValue(): TextFieldValue = TextFieldValue(
    annotatedString = paperString.toAnnotatedString(),
    selection = selection,
    composition = composition
)

internal fun PaperEditorValue.plusSpanStyle(
    spanStyle: PaperString.Range<SpanStyle>
): PaperEditorValue = PaperEditorValue(
    paperString = paperString.copy(
        spanStyles = paperString.spanStyles + spanStyle
    ),
    selection = selection,
    composition = composition
)

/**
 * We need to check before adding new paragraph style for intersecting range
 */
internal fun PaperEditorValue.plusParagraphStyle(
    paragraphStyle: PaperString.Range<ParagraphStyle>
): PaperEditorValue = PaperEditorValue(
    paperString = paperString.copy(
        paragraphStyles = paperString.paragraphStyles + paragraphStyle
    ),
    selection = selection,
    composition = composition
)
