package com.tanaka.mazivanhanga.tanakaanimeprototype.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tanaka.mazivanhanga.tanakaanimeprototype.util.CustomTypeConverters


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */

@Database(entities = [LatestShowEpisodeEntity::class], version = 2)
@TypeConverters(CustomTypeConverters::class)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao

    companion object {
        val DATA_BASE_NAME: String = "anime_db"
    }
}