package com.beetlestance.paper.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.beetlestance.paper.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteDao : EntityDao<Note>() {

    @Transaction
    @Query(value = ALL_NOTES_QUERY)
    abstract fun notesObservable(): Flow<List<Note>>

    @Transaction
    @Query(value = "$ALL_NOTES_QUERY where id = :id")
    abstract fun noteObservable(id: Long): Flow<Note>


    @Query(value = ALL_NOTES_QUERY)
    abstract suspend fun allNotes(): List<Note>

    companion object {
        private const val ALL_NOTES_QUERY = "SELECT * FROM ${PaperTables.NOTE_TABLE}"
    }
}