package com.example.alertlocation_kotlin


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alertlocationkotlin.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
 import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val TOPIC ="/topics/myTopic"
    var groupNotificationKey=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        ///when app is in foreground it works
        CoroutineScope(Dispatchers.IO).launch {
            val response= RetrofitInstance.api.createGroupNotification(
                    Group(
                            "create", "ae4", mutableListOf<String>(
                            "c3SlAfeXAdM:APA91bENahSjfpSyaYvC4DrAzukuw_Ivqs6UTPFiDHrb7OPUrT5ycknyzFACWuWZ5xtVhWpkxARLjBnEmkRdda-0STViBOc8LzQCYT3E-Lx_HGzT8SajEHcucHM-t9VHfNwWzZKqSg4z",
                            "f1OmDKGL6LM:APA91bFPfOfoiFXowuSCfB2WtFdbPmbT7buj86jSo-sqoNPsxL_2JV9PVsXy-RSN85zDEcoPfJG7HXQOMcpk6BK5a9VhhwbG3pl8Lo7RwCGtlCECYtu5_J943SZVy_Qa5hXEe4ypDePF",
                            "f79ZXvS40OM:APA91bEImUK3olDAp70hP921c-QT1G2avRCz8O5Zah0HHHYyYPtuO7YwpES0Scr9jefs4PWRsUvOFKMfntVWtJiWnL5Dl8bjvTyzOnEfGsf3Gjqo-PWBzVR0ZLxxN6ncwDTzvnSJJYJA",
                            "fQ1mp6FVWps:APA91bECKums6zw4FBZ10XvXWGDA8qAP5h_9v0NVa72TH7N_W9kFgsXQEI_-3fLHeL9oHNs1dx9hEP0mkPptlRF2mOVTHh6OYaMb0jbh6bUkeMGtSEqjUq2-u_z_mJ_pvRu3se0nEe3m"

                    )
                    )
            )//https://firebase.google.com/docs/cloud-messaging/android/device-group?utm_source=studio
            if(response.isSuccessful){
                //    NotificationGroupResponse(res)
                println("reeesponse ${response.message()}")
                val notificationGroupResponse: NotificationGroupResponse? = response.body()
                println("reeesponse ${notificationGroupResponse?.notification_key}")
                groupNotificationKey=notificationGroupResponse?.notification_key!!
            }
        }

        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                //   Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    //auto einai to sosto
    private fun sendNotificationWhenAppClosed() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var not=Group_DataNotification(
                    Data("titleadasdsad", "message", "MainActivity"), groupNotificationKey
            )
            val response = RetrofitInstance.api.createDataGroupNotification(not)
            if(response.isSuccessful) {
                //   Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
    data class UserNameToken(var username: String, var token: String)
}