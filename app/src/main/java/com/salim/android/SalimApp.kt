package com.salim.android

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SalimApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("SalimApp", "Uncaught exception in thread ${thread.name}", throwable)
            // Let the system handle the crash naturally so it's visible in crash reports
        }
    }
}
