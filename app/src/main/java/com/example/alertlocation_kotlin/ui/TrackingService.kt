package com.example.alertlocation_kotlin.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager


import com.androiddevs.runningappyt.other.TrackingUtility
import com.example.alertlocation_kotlin.Constants
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_PAUSE_SERVICE
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_STOP_SERVICE
import com.example.alertlocation_kotlin.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.example.alertlocation_kotlin.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.example.alertlocation_kotlin.Constants.Companion.NOTIFICATION_ID
import com.example.alertlocation_kotlin.MainActivity
import com.example.alertlocation_kotlin.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Log.d("main","Resuming service...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("main","Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("main","Stopped service")
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {

                val request =createLocationRequest()
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )

        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval= Constants.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval= Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }


    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val intent = Intent(Constants.ACTION_BROADCAST)
            intent.putExtra(Constants.EXTRA_LOCATION, locationResult?.lastLocation)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            println("locationnn ${locationResult?.lastLocation?.latitude}")
        }
    }




    private fun startForegroundService() {

        isTracking.postValue(true)


        val servicePendingIntent = PendingIntent.getService(
            this, 0, Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val text: CharSequence = "Utils.getLocationText(mLocation)"
        val title: CharSequence = Utils.getLocationTitle(this)
        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                MainActivity::class.java
            ), 0
        )


        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .addAction(
                R.drawable.common_google_signin_btn_icon_dark, "Launch app",
                activityPendingIntent
            )
            .addAction(
                R.drawable.common_google_signin_btn_icon_dark, "Stop service",
                servicePendingIntent
            )
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_add_24)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}














