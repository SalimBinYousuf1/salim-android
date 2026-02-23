package com.salim.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.salim.android.service.SalimForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in listOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED)) {
            context.startForegroundService(Intent(context, SalimForegroundService::class.java))
        }
    }
}
