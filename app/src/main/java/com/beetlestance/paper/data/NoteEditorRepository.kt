package com.beetlestance.paper.data

import com.beetlestance.paper.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteEditorRepository @Inject constructor(
    private val noteDao: NoteDao
) {

    fun observeNote(): Flow<Note?> = noteDao.notesObservable()
        .map { it.firstOrNull() }

    suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.insertOrUpdate(note)
        }
    }

}