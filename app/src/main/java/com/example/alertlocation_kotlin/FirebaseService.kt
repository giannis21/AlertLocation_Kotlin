package com.example.alertlocationkotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.alertlocation_kotlin.MainActivity
import com.example.alertlocation_kotlin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
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
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Check if message contains a data payload.

        // Check if message contains a data payload.
        var a= message.data["title"]
        var b= message.data["text"]
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

//            val title: String = message.getNotification()?.getTitle()!! //get title
//            val message1: String = message.getNotification()?.getBody()!! //get message
//            val click_action: String = message.getNotification()?.clickAction!! //get click_action
//            Log.d("TAG", "Message Notification Title: $title")
//            Log.d("TAG", "Message Notification Body: $message1")
//            Log.d("TAG", "Message Notification click_action: $click_action")

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["text"]))

                .setContentText(message.data["text"])
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
        notificationManager.createNotificationChannel(channel)
    }

}

