package com.tanaka.mazivanhanga.tanakaanimeprototype.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Tanaka Mazivanhanga on 07/31/2020
 */
object ApiHandler {
    private var _baseURL = "http://10.147.1.162:8004/"
    val BASE_URL get() = _baseURL
    var animeService: Service
    var videoService: Service
    var videoClient: OkHttpClient

    var okHttpClient: OkHttpClient

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()

        videoClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS).addInterceptor(
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                })
            .build()

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                })
            .build()

        animeService =
            getRetrofitBuilder(BASE_URL).build()
                .create(
                    Service::class.java
                )
        videoService = getRetrofitBuilder(BASE_URL).build()
            .create(
                Service::class.java
            )
    }

    fun setBaseUrl(url: String) {
        println(url)
        _baseURL = url
        animeService = getRetrofitBuilder(BASE_URL).build().create(Service::class.java)
        videoService = getRetrofitBuilder(BASE_URL).build().create(Service::class.java)
    }

    private fun getRetrofitBuilder(endPoint: String): Retrofit.Builder {
        val client = okHttpClient
        return Retrofit.Builder().baseUrl(endPoint)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }


}