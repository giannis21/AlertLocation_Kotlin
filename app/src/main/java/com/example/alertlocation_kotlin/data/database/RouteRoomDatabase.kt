package com.example.alertlocation_kotlin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alertlocation_kotlin.data.model.Route
import com.example.tvshows.Converters
import com.example.tvshows.RouteDao

@Database(entities = [Route::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class RouteRoomDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao

    companion object {

        @Volatile
        private var INSTANCE: RouteRoomDatabase? = null

        fun getDatabase(context: Context): RouteRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                  return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RouteRoomDatabase::class.java,
                    "Alert_loc"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}