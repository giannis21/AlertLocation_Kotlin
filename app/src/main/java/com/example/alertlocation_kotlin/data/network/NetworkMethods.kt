package com.example.alertlocation_kotlin
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities



object netMethods {

 //   var listener: InternetCallback? = null

    fun hasInternet(applicationContext: Context, return_boolean_state:Boolean): Boolean {
        return true
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!isInternetAvailable1(applicationContext)) {
//                listener?.hasInternet(false)
//                if(return_boolean_state==false)
//                    throw NoInternetException("No internet connection")
//                else
//                    return false
//             } else {
//                listener?.hasInternet(true)
//                return true
//            }
//        } else {
//            if (!isInternetAvailable(applicationContext)) {
//                listener?.hasInternet(false)
//                if(return_boolean_state==false)
//                    throw NoInternetException("No internet connection")
//                else
//                    return false
//            } else {
//                listener?.hasInternet(true)
//                return true
//            }
//        }
    }

    fun isInternetAvailable(applicationContext: Context): Boolean {

        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.activeNetworkInfo.also {
            return it != null && it.isConnected
        }


    }

    fun isInternetAvailable1(applicationContext: Context): Boolean {
        var result = false
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }
}