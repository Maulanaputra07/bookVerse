package com.example.bookverse

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationId = 1
const val channelID = "channel1"
const val titleExtra = "BookVerse"
const val messageExtra = "cihuyyy"

class Notification: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = context?.let {
            NotificationCompat.Builder(it, channelID)
                .setSmallIcon(R.drawable.logo_dark)
                .setContentTitle(intent?.getStringExtra(titleExtra))
                .setContentText(intent?.getStringExtra(messageExtra))
                .build()
        }

        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

}