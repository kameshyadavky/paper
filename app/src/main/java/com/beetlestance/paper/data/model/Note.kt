package com.beetlestance.paper.data.model

import androidx.room.*
import com.beetlestance.paper.common.toDataClass
import com.beetlestance.paper.common.toJsonString
import com.beetlestance.paper.data.PaperTables
import com.google.gson.Gson

@Entity(
    tableName = PaperTables.NOTE_TABLE
)
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    val heading: String,
    val time: Long = 0,

    @TypeConverters(BodyConvertor::class)
    val body: List<Body>?

) : PaperEntity {
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


object BodyConvertor {
    @TypeConverter
    fun fromDatabase(value: String?): List<Note.Body>? {
        return value?.toDataClass()
    }

    @TypeConverter
    fun toDatabase(list: List<Note.Body>?): String? {
        return list?.toJsonString()
    }
}