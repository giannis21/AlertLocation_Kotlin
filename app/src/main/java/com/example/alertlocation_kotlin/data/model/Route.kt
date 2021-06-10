package com.example.alertlocation_kotlin.data.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.tvshows.Converters

@TypeConverters(Converters::class)
@Entity(tableName = "Route", primaryKeys = ["RouteName"])
public data class Route(
    var RouteName: String,
    var users: MutableList<User>,
    var points: MutableList<Points>,
    var message: String
) {
    fun getReceivers(): String {
        var buildString = ""
        users.forEachIndexed { index, user ->
            buildString += user
        }
        return buildString
    }
}