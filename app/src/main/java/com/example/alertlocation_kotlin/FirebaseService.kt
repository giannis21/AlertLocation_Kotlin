package com.example.alertlocationkotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.alertlocation_kotlin.InitApp.Companion.instance
import com.example.alertlocation_kotlin.MainActivity
import com.example.alertlocation_kotlin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    companion object{
        var sharedPref :SharedPreferences ?=null

        var token: String?
        get() {
            return sharedPref?.getString("token", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("token", value)?.apply()
        }

        var uniqueId: String?
        get() {
            return sharedPref?.getString("uniqueId", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("uniqueId", value)?.apply()
        }

        var isVibrate: Boolean?
            get() {
                return sharedPref?.getBoolean("vibrate", false)
            }
            set(value) {
                sharedPref?.edit()?.putBoolean("vibrate", value!!)?.apply()
            }

        var isRinging: Boolean?
            get() {
                return sharedPref?.getBoolean("isRinging", false)
            }
            set(value) {
                sharedPref?.edit()?.putBoolean("isRinging", value!!)?.apply()
            }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Check if message contains a data payload.

        // Check if message contains a data payload.
        var title= message.data["title"]
        var messageTxt= message.data["message"]
        val time= message.data["time"]
        val senderId= message.data["sender"]
        val location= message.data["location"]

        val intent = Intent(this, MainActivity::class.java).apply {
            flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("timeSent", time)
            putExtra("senderId", senderId)
            putExtra("userLocation", location)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }


            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        if (isVibrate!!)
            vibratePhone()

        if (isRinging!!)
            playSound1()
        notificationManager.createNotificationChannel(channel)
    }

    fun playSound1() {
        try {
            val player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI)
            player.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun vibratePhone() {
        val vibrator = instance.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26)
       // val vibrationEffect = VibrationEffect.createOneShot(HAPTIC_FEEDBACK_DURATION, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

