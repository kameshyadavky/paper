package com.beetlestance.paper.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.beetlestance.paper.data.model.BodyConvertor
import com.beetlestance.paper.data.model.Note

@Database(
    entities = [
        Note::class
    ],
    version = 1
)
@TypeConverters(BodyConvertor::class)
abstract class PaperRoomDatabase : RoomDatabase(), PaperDatabase
