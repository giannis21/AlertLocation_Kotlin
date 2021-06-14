package com.example.alertlocation_kotlin.ui

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
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
import com.example.alertlocationkotlin.CHANNEL_ID
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val intent = Intent(Constants.ACTION_BROADCAST)
                intent.putExtra(Constants.EXTRA_LOCATION, locationResult?.lastLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val startedFromNotification = intent!!.getBooleanExtra(
            Constants.EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )

        // We got here because the user decided to remove location updates from the notification.

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            val intent = Intent(Constants.ACTION_BROADCAST)
            intent.putExtra(Constants.EXTRA_LOC_STOPED, "service stoped")
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            stopSelf()
        }

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
                    mLocationCallback!!,
                    Looper.getMainLooper()
                )

        } else {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback!!)
        }
    }
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval= Constants.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval= Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }







    private fun startForegroundService() {

        isTracking.postValue(true)
        startForeground()
    }


    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(CHANNEL_ID, "channelName")
            } else {
                ""
            }

        val intent = Intent(this, TrackingService::class.java)
        intent.putExtra(Constants.EXTRA_STARTED_FROM_NOTIFICATION, true)

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        val servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val activityPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
            .addAction(
                R.drawable.common_google_signin_btn_icon_dark, "Launch app",
                activityPendingIntent
            )
            .addAction(
                R.drawable.common_google_signin_btn_icon_dark, "Stop service",
                servicePendingIntent
            )
            .setContentText("Service started")
          //  .setContentTitle("titlos")
            .setOngoing(true)
        val millis=System.currentTimeMillis()
        val notification = notificationBuilder.setOngoing(true)

            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(millis.toInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }





    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback!!)
    }
}














