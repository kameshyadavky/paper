package com.beetlestance.paper.data

import com.beetlestance.paper.data.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NoteEditorRepository @Inject constructor() {
    private val _noteItems: MutableStateFlow<Note> = MutableStateFlow(Note.Empty)
    val noteItems = _noteItems.asStateFlow()

    fun updateNote(note: Note) {
        _noteItems.tryEmit(note)
    }

}