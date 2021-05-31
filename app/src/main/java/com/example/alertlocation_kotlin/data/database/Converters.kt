package com.example.tvshows

import androidx.room.TypeConverter
import com.example.alertlocation_kotlin.data.model.Points
import com.example.alertlocation_kotlin.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun fromStringToList(value: String?): MutableList<Points?>? {
        val listType = object : TypeToken<MutableList<Points?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString(list: MutableList<Points?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

     @TypeConverter
    fun fromStringToList3(value: String?): MutableList<User?>? {
        val listType = object : TypeToken<MutableList<User>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListtoString3(list: MutableList<User?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }



}