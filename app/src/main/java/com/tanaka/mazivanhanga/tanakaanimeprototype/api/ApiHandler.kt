package com.tanaka.mazivanhanga.tanakaanimeprototype.api

import com.google.gson.GsonBuilder
import com.tanaka.mazivanhanga.tanakaanimeprototype.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Tanaka Mazivanhanga on 07/31/2020
 */
object ApiHandler {

    val animeService: Service by lazy {
        getRetrofitBuilder(BuildConfig.BASE_URL)
            .build()
            .create(Service::class.java)
    }

    val gson = GsonBuilder().create()

    private fun getRetrofitBuilder(endPoint: String): Retrofit.Builder {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val client = OkHttpClient().newBuilder().addInterceptor(
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            }
        ).build()
        return Retrofit.Builder().baseUrl(endPoint)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }


}