package com.example.alertlocation_kotlin

import android.app.Application

class InitApp:Application(){


    override fun onCreate() {
        super.onCreate()
        instance=this
    }
    companion object{
        lateinit var instance:InitApp
    }
}