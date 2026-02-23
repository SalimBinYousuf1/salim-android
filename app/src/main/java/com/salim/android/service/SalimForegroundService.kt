package com.salim.android.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.salim.android.MainActivity
import com.salim.android.R

class SalimForegroundService : Service() {
    companion object {
        const val CHANNEL_ID = "salim_channel"
        const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Salim is active")
            .setContentText("AI agent running 24/7")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Salim Service", NotificationManager.IMPORTANCE_LOW).apply {
            description = "Keeps Salim AI agent running"
        }
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }
}
