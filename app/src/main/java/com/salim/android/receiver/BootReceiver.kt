package com.salim.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import com.salim.android.service.SalimForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in listOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED)) return

        // On Android 24+, BOOT_COMPLETED fires while the device may still be locked (direct boot).
        // We must not start services that access credential-encrypted storage in that state.
        // Wait until the user has unlocked (UserManager.isUserUnlocked) before starting.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
            if (!userManager.isUserUnlocked) return
        }

        try {
            context.startForegroundService(Intent(context, SalimForegroundService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
