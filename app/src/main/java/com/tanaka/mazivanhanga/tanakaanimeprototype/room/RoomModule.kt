package com.tanaka.mazivanhanga.tanakaanimeprototype.room

import android.content.Context
import androidx.room.Room

object RoomModule {
    fun getAnimeDb(context: Context): AnimeDatabase {
        return Room.databaseBuilder(
            context,
            AnimeDatabase::class.java,
            AnimeDatabase.DATA_BASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
    
    fun getAnimeDao(animeDatabase: AnimeDatabase):AnimeDao{
        return animeDatabase.animeDao()
    }
}