package com.beetlestance.paper.di

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.beetlestance.paper.data.NoteDao
import com.beetlestance.paper.data.PaperDatabase
import com.beetlestance.paper.data.PaperRoomDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomDatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PaperRoomDatabase {
        val builder = Room.databaseBuilder(context, PaperRoomDatabase::class.java, "paper.db")

        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {

    @Provides
    fun provideAphidRecipes(db: PaperDatabase): NoteDao = db.noteDao()
}

@InstallIn(SingletonComponent::class)
@Module
abstract class DatabaseModuleBinds {

    @Binds
    abstract fun bindAphidDatabase(db: PaperRoomDatabase): PaperDatabase
}
