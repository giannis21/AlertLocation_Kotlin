package com.example.alertlocation_kotlin.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_BROADCAST
import com.example.alertlocation_kotlin.Constants.Companion.CHANNEL_ID
import com.example.alertlocation_kotlin.Constants.Companion.EXTRA_LOCATION
import com.example.alertlocation_kotlin.Constants.Companion.EXTRA_STARTED_FROM_NOTIFICATION
import com.example.alertlocation_kotlin.Constants.Companion.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
import com.example.alertlocation_kotlin.Constants.Companion.NOTIFICATION_ID
import com.example.alertlocation_kotlin.Constants.Companion.UPDATE_INTERVAL_IN_MILLISECONDS
import com.example.alertlocation_kotlin.MainActivity
import com.example.alertlocation_kotlin.R
import com.google.android.gms.location.*
import java.util.*

class LocationUpdatesService: Service() {
    val TAG = "resPOINT"
    val mBinder: IBinder = LocalBinder()

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */


    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    var mChangingConfiguration = false

    private var mNotificationManager: NotificationManager? = null

    /**
     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
     */
    private var mLocationRequest: LocationRequest? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null

    private var mServiceHandler: Handler? = null

    var latitude: Double? = null
    var longitude:Double? = null

    /**
     * The current location.
     */
    private var mLocation: Location? = null


    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }

        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create().apply {
            interval=UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval= FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }

    fun getLastLocation(){
        mFusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                location?.let {
                    mLocation = it
                }
            }
    }

    /**
     * Realtime location save in firestore or firebase */
//    var geoFire: GeoFire? = null
//    var firebaseFirestore: FirebaseFirestore? = null
//    var documentReference: DocumentReference? = null
//    var firebaseAuth: FirebaseAuth? = null

    private fun onNewLocation(location: Location) {
        Log.d(TAG, "New location: $location")
        mLocation = location

        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager!!.notify(NOTIFICATION_ID, getNotification())

            // Getting location when notification was call.
            latitude = location.latitude
            longitude = location.longitude

            // Here using to call Save to serverMethod
            SavetoServer()
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()")
        stopForeground(true)
        mChangingConfiguration = false

        // Register Firestore when service will restart

        // Register Firestore when service will restart
//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseFirestore = FirebaseFirestore.getInstance()
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.

        Log.i(TAG, "in onRebind()")
        stopForeground(true)
        mChangingConfiguration = false

        // Register Firestore when service will restart

        // Register Firestore when service will restart
//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseFirestore = FirebaseFirestore.getInstance()
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Log.i(TAG, "Last client unbound from service")

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.

        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.d(TAG, "Starting foreground service")

            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
             //   mNotificationManager.startServiceInForeground(Intent(applicationContext, LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification())
            }
            // startForeground(LocationUpdatesService.NOTIFICATION_ID, getNotification())
        }
        return true // Ensures onRebind() is called when a client re-binds.

    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        Utils.setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest!!, mLocationCallback!!, Looper.myLooper()!!)
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, false)
            Log.d(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            Utils.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, true)
            Log.d(
                TAG,
                "Lost location permission. Could not remove updates. $unlikely"
            )
        }
    }



    private fun getNotification(): Notification? {
        val intent = Intent(this, LocationUpdatesService::class.java)


        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        val servicePendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val text: CharSequence = Utils.getLocationText(mLocation)
        val title: CharSequence = Utils.getLocationTitle(this)
        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                MainActivity::class.java
            ), 0
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .addAction(
                R.drawable.common_google_signin_btn_icon_dark, "Launch app",
                activityPendingIntent
            )
            .addAction(
                R.drawable.common_google_signin_btn_icon_dark, "Stop service",
                servicePendingIntent
            )
            .setContentText(text)
            .setContentTitle("titlos")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("text")
            .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG, "Service started")
        val startedFromNotification = intent!!.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )

        // We got here because the user decided to remove location updates from the notification.

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        // Tells the system to not try to recreate the service after it has been killed.
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY
    }


    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LocationUpdatesService = this@LocationUpdatesService
    }
    /**
     * Returns true if this is a foreground service.
     *
     * @param context The [Context].
     */
    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(
            ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }


    /**
     * Save a value in realtime to firestore when user in background
     * For foreground you have to call same method to activity
     */
    private fun SavetoServer() {
        Toast.makeText(this, "Save to server", Toast.LENGTH_SHORT).show()
        Log.d("resMM", "Send to server")
        Log.d("resML", latitude.toString())
        Log.d("resMLL", longitude.toString())
        val driverMap: MutableMap<String, String> = HashMap()
        driverMap["name"] = latitude.toString()
        driverMap["email"] = longitude.toString()
//        documentReference = firebaseFirestore.collection("driverAvaliable").document("newdriver")
//        documentReference.update(
//            "latitude", latitude.toString(),
//            "longitude", longitude.toString(),
//            "timeStamp", FieldValue.serverTimestamp()
//        )
//            .addOnSuccessListener(OnSuccessListener<Void?> {
//                Log.d(
//                    LocationUpdatesService.TAG,
//                    "DocumentSnapshot successfully updated!"
//                )
//            }).addOnFailureListener(
//                OnFailureListener { e ->
//                    Log.d(
//                        LocationUpdatesService.TAG,
//                        "Error updating document",
//                        e
//                    )
//                })
    }

    override fun onDestroy() {
        super.onDestroy()
        println("dddddd service destroyed")
        mServiceHandler!!.removeCallbacksAndMessages(null)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }
}
