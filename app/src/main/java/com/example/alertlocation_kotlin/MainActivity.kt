package com.example.alertlocation_kotlin


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.alertlocationkotlin.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
 import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.banner_layout.view.*

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


        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }
//    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val response = RetrofitInstance.api.postNotification(notification)
//            if(response.isSuccessful) {
//                //   Log.d(TAG, "Response: ${Gson().toJson(response)}")
//            } else {
//                Log.e(TAG, response.errorBody().toString())
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, e.toString())
//        }
//    }

    //auto einai to sosto
//    private fun sendNotificationWhenAppClosed() = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            var not=Group_DataNotification(
//                    Data("titleadasdsad", "message", "MainActivity"), groupNotificationKey
//            )
//            val response = RetrofitInstance.api.createDataGroupNotification(not)
//            if(response.isSuccessful) {
//                //   Log.d(TAG, "Response: ${Gson().toJson(response)}")
//            } else {
//                Log.e(TAG, response.errorBody().toString())
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, e.toString())
//        }
//    }

    fun showBanner(value: String, success: Boolean = false) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {
            frameLayout_main?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.redBannerTxtV.text = value

                if(!success){
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.LightRed)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)

                }else{
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.success_color)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_save)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 4000)
            }
        }
    }

}