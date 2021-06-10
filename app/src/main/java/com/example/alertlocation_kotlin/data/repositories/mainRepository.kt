package com.example.alertlocation_kotlin.data.repositories

import androidx.lifecycle.LiveData
import com.example.alertlocation_kotlin.data.model.Route
import com.example.tvshows.RouteDao

class mainRepository(private val routeDao: RouteDao) {

    suspend fun addRoute(route:  Route): Int{
        routeDao.insert(route)
        return 1
    }

    fun getAll(): LiveData<MutableList<Route>> {
       return routeDao.getRoutes()
    }

}