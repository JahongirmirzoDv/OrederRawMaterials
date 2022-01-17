package com.example.orderrawmaterials.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.orderrawmaterials.AdminPanel
import com.example.orderrawmaterials.MainActivity
import com.example.orderrawmaterials.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Messaging : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMessagingServ"
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.d(TAG, "onMessageReceived: ${p0.notification?.title}")
        Log.d(TAG, "onMessageReceived: ${p0.notification?.body}")
        Log.d(TAG, "onMessageReceived: ${p0.data}")
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, "channelId")
            .setSound(defaultUri, STREAM_NOTIFICATION)
        builder.setSmallIcon(R.drawable.logo)
        builder.setContentTitle(p0.data["title"])
        builder.setContentText(p0.data["body"])
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        val notification = builder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("channelId", "Name", importance)
            mChannel.description = "descriptionText"
            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(1, notification)
    }

}