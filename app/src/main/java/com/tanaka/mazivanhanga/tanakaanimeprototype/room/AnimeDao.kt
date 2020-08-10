
package com.tanaka.mazivanhanga.tanakaanimeprototype.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */
@Dao
interface AnimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cacheEntity: LatestShowEpisodeEntity): Long

    @Query(value = "SELECT * FROM latestEpisodes")
    fun getLatestShows(): Single<List<LatestShowEpisodeEntity>>

}