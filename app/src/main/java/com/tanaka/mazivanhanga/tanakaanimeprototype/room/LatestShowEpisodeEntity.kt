package com.tanaka.mazivanhanga.tanakaanimeprototype.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */
@Entity(tableName = "latestEpisodes")
data class LatestShowEpisodeEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "url") val url: String,
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "current_ep_url") val currentEpURL: String,
    @ColumnInfo(name = "current_ep") val currentEp: String
)