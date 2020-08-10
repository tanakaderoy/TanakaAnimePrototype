package com.tanaka.mazivanhanga.tanakaanimeprototype.models

import java.io.Serializable

/**
 * Created by Tanaka Mazivanhanga on 08/07/2020
 */
data class LatestShow(
    var title: String,
    val image: String,
    val url: String,
    val currentEpURL: String,
    var currentEp: String
) : Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LatestShow

        if (title != other.title) return false
        if (image != other.image) return false
        if (url != other.url) return false
        if (currentEpURL != other.currentEpURL) return false
        if (currentEp != other.currentEp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + currentEpURL.hashCode()
        result = 31 * result + currentEp.hashCode()
        return result
    }
}