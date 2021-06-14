package com.example.alertlocation_kotlin

import android.content.Context
import com.example.alertlocation_kotlin.netMethods

import okhttp3.Interceptor

import okhttp3.Response


class NetworkConnectionIncterceptor(context: Context) : Interceptor {

    private val applicationContext: Context = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        netMethods.hasInternet(applicationContext,false)
        return chain.proceed(chain.request())
    }
}