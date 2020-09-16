package com.tanaka.mazivanhanga.tanakaanimeprototype.api

import android.content.Context
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.*
import com.tanaka.mazivanhanga.tanakaanimeprototype.room.LatestShowEpisodeCacheMapper
import com.tanaka.mazivanhanga.tanakaanimeprototype.room.RoomModule
import io.reactivex.Single


/**
 * Created by Tanaka Mazivanhanga on 07/31/2020
 */
class Repository(private val context: Context) {

    companion object {
        val TAG = Repository::class.simpleName
        fun getInstance(context: Context): Repository {
            return Repository(context)
        }
    }

    val db = RoomModule.getAnimeDb(context)
    val animeDao = RoomModule.getAnimeDao(db)
    val latestShowMapper = LatestShowEpisodeCacheMapper()

    fun getLatestShows(): Single<List<LatestShow>> {
        return ApiHandler.animeService.getLatestShows().flatMap {
            val episodes = (it.body() as LatestShowResponse).latestShows
            episodes.forEach { ep ->
                animeDao.insert(latestShowMapper.mapToEntity(ep))
            }
            return@flatMap animeDao.getLatestShows()
        }.map {
            return@map latestShowMapper.mapFromEntitiesList(it)
        }
    }

    fun getVideo(body: VideoRequestBody): Single<VideoModel> {
        return ApiHandler.videoService.getVideo(body).map {
            return@map (it.body() as VideoModel)
        }
    }

    fun getSearchResults(query: String): Single<List<SearchResult>> {
        return ApiHandler.animeService.getSearchResults(query)
    }

    fun getEpisodes(show:String):Single<List<Episode>>{
        return ApiHandler.animeService.getEpisodes(show)
    }
}