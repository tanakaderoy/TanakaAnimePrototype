package com.tanaka.mazivanhanga.tanakaanimeprototype.util


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */
sealed class DataState<out R> {

    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
}