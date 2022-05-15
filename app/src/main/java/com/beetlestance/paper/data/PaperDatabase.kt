package com.beetlestance.paper.data

interface PaperDatabase {
    fun noteDao(): NoteDao
}

internal object PaperTables {
    const val NOTE_TABLE: String = "note_table"
}