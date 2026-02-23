package com.salim.android

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class SalimApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("SalimApp", "Uncaught exception in thread ${thread.name}", throwable)
            // Optionally restart the app or just let it die
            exitProcess(1)
        }
    }
}
