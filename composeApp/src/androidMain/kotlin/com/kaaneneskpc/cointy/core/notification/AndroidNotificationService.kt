package com.kaaneneskpc.cointy.core.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kaaneneskpc.cointy.MainActivity
import com.kaaneneskpc.cointy.R

class AndroidNotificationService(
    private val context: Context
) : NotificationService {
    override fun showPriceAlertNotification(
        title: String,
        message: String,
        coinId: String,
        alertId: Long
    ) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "Notification permission not granted")
            return
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_COIN_ID, coinId)
            putExtra(EXTRA_ALERT_ID, alertId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotificationManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(alertId.toInt(), notification)
            Log.d(TAG, "Notification sent: $title")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when sending notification", e)
        }
    }
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    override fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    override fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
    companion object {
        private const val TAG = "AndroidNotificationService"
        const val EXTRA_COIN_ID = "extra_coin_id"
        const val EXTRA_ALERT_ID = "extra_alert_id"
    }
}

