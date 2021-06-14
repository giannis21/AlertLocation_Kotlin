package com.example.alertlocation_kotlin.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.tvshows.Converters

@TypeConverters(Converters::class)
@Entity(tableName = "Route")
public data class Route(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var RouteName: String,
    var users: MutableList<User>,
    var points: MutableList<Points>,
    var message: String,
    var notificationGroupId: String
) {
    @Ignore
    var isExpanded:Boolean=false

    var isEnabled:Boolean=false
    fun getReceivers(): String {
        var buildString = ""
        users.forEachIndexed { index, user ->
            buildString += user
        }
        return buildString
    }
}