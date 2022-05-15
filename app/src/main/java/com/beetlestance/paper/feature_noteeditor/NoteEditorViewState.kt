package com.beetlestance.paper.feature_noteeditor

import androidx.compose.runtime.Immutable
import com.beetlestance.paper.data.model.Note
import com.beetlestance.paper.editor.PaperEditorValue

@Immutable
data class NoteEditorViewState(
    val bodyItems: List<Any> = listOf(NoteEditorValue(editorValue = PaperEditorValue())),
    val heading: String = "",
    val time: String = "",
    val note: Note? = null,
    val selectedIndex: Int = 0
) {
    companion object {
        val Empty = NoteEditorViewState()
    }
}

data class NoteImage(
    val path: String,
    val widthPercentage: Float = 1f
)

data class NoteEditorValue(
    val editorValue: PaperEditorValue
) {
    companion object {
        val Empty = NoteEditorValue(PaperEditorValue())
    }
}