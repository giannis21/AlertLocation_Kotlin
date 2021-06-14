package com.example.alertlocation_kotlin.ui

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.example.alertlocation_kotlin.R
import java.text.DateFormat
import java.util.*


object Utils {

    const val  KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    fun requestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    fun setRequestingLocationUpdates(context: Context ,  requestingLocationUpdates:Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply();
    }

    fun getLocationText(location: Location?):String {
        location?.let {
            return "Unknown location"
        } ?: kotlin.run {
            return  "(" + location?.latitude + ", " + location?.longitude + ")"
        }

    }

    fun getLocationTitle(context: Context ): String {
        return context.getString(
            R.string.location_updated,
            DateFormat.getDateTimeInstance().format( Date()));
    }


}