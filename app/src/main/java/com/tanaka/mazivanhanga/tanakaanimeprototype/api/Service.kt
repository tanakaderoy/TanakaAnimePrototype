package com.tanaka.mazivanhanga.tanakaanimeprototype.api;

import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShowResponse
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoRequestBody
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


/**
 * Created by Tanaka Mazivanhanga on 07/31/2020
 */
interface Service {
    @GET(value = "shows/home/")
    fun getLatestShows(): Single<Response<LatestShowResponse>>

    @POST(value = "watch/")
    fun getVideo(@Body body: VideoRequestBody): Single<Response<VideoModel>>
}