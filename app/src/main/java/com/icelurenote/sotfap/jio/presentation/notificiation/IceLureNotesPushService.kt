package com.icelurenote.sotfap.jio.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.icelurenote.sotfap.IceLureNotesActivity
import com.icelurenote.sotfap.R
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication

private const val ICE_LURE_NOTES_CHANNEL_ID = "ice_lure_notes_notifications"
private const val ICE_LURE_NOTES_CHANNEL_NAME = "IceLureNotes Notifications"
private const val ICE_LURE_NOTES_NOT_TAG = "IceLureNotes"

class IceLureNotesPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                iceLureNotesShowNotification(it.title ?: ICE_LURE_NOTES_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                iceLureNotesShowNotification(it.title ?: ICE_LURE_NOTES_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            iceLureNotesHandleDataPayload(remoteMessage.data)
        }
    }

    private fun iceLureNotesShowNotification(title: String, message: String, data: String?) {
        val iceLureNotesNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ICE_LURE_NOTES_CHANNEL_ID,
                ICE_LURE_NOTES_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            iceLureNotesNotificationManager.createNotificationChannel(channel)
        }

        val iceLureNotesIntent = Intent(this, IceLureNotesActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val iceLureNotesPendingIntent = PendingIntent.getActivity(
            this,
            0,
            iceLureNotesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val iceLureNotesNotification = NotificationCompat.Builder(this, ICE_LURE_NOTES_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ice_lure_notes_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(iceLureNotesPendingIntent)
            .build()

        iceLureNotesNotificationManager.notify(System.currentTimeMillis().toInt(), iceLureNotesNotification)
    }

    private fun iceLureNotesHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}