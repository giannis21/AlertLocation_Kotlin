package com.example.alertlocation_kotlin.data.model

import androidx.room.Entity

@Entity(tableName = "Route", primaryKeys = ["RouteName"])
data class Route(
        private var RouteName: String,
        private var users: MutableList<User>,
        private var points: MutableList<Points>
)