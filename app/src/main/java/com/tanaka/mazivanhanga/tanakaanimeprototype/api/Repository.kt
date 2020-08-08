package com.tanaka.mazivanhanga.tanakaanimeprototype.api

import androidx.lifecycle.MutableLiveData
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShowResponse
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoRequestBody
import io.reactivex.Single


/**
 * Created by Tanaka Mazivanhanga on 07/31/2020
 */
object Repository {
    val TAG = Repository::class.simpleName

    fun getLatestShows(): Single<List<LatestShow>> {
        val data = MutableLiveData<List<LatestShow>>()
        return ApiHandler.animeService.getLatestShows().map {
            return@map (it.body() as LatestShowResponse).latestShows
        }
    }

    fun getVideo(body: VideoRequestBody): Single<VideoModel> {
        return ApiHandler.animeService.getVideo(body).map {
            return@map (it.body() as VideoModel)
        }
    }
}