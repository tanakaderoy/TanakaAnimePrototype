package com.tanaka.mazivanhanga.tanakaanimeprototype.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */
class CustomTypeConverters {
    var gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<String?>? {
        val listType =
            object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String?>?): String? {
        return gson.toJson(list)
    }


}
