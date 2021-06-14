package com.example.alertlocation_kotlin.data.repositories

import androidx.lifecycle.LiveData
import com.example.alertlocation_kotlin.data.model.Route
import com.example.tvshows.RouteDao
import kotlinx.coroutines.flow.Flow

class mainRepository(private val routeDao: RouteDao) {

    suspend fun addRoute(route:  Route): Int{
        routeDao.insert(route)
        return 1
    }

    suspend fun updateFriendlyName(id: Long, name: String): Int{
        routeDao.update(id,name)
        return 1
    }
    suspend fun enableRoute(id: Long, isEnabled: Boolean): Int{
        routeDao.updateSwitch(id,isEnabled)
        return 1
    }

    suspend fun removeItem(route:Route): Int{
        routeDao.removeItem(route)
        return 1
    }

    fun getAll(): Flow<MutableList<Route>> {
       return routeDao.getRoutes()
    }



}