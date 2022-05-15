package com.beetlestance.paper.data

import com.beetlestance.paper.common.toJsonString
import com.beetlestance.paper.noteeditor.NoteImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import javax.inject.Inject


data class Note(
    val heading: String,
    val time: Long = 0,
    val body: List<Body>?
) {
    data class Body(
        val type: Int,
        val body: String
    ) {
        companion object {
            const val Text = 0
            const val Image = 1
        }
    }

    companion object {
        val Empty = Note(
            heading = "",
            time = System.currentTimeMillis(),
            body = null
        )
    }
}

class NoteEditorRepository @Inject constructor() {
    private val _noteItems: MutableStateFlow<Note> = MutableStateFlow(Note.Empty)
    val noteItems = _noteItems.asStateFlow()

    fun updateNote(note: Note) {
        _noteItems.tryEmit(note)
    }

}