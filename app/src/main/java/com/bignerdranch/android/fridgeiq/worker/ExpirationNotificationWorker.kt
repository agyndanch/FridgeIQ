package com.bignerdranch.android.fridgeiq.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bignerdranch.android.fridgeiq.MainActivity
import com.bignerdranch.android.fridgeiq.R
import com.bignerdranch.android.fridgeiq.data.database.FridgeIQDatabase
import java.util.*
import java.util.concurrent.TimeUnit

class ExpirationNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = FridgeIQDatabase.getDatabase(context)
            val foodItemDao = database.foodItemDao()

            // Get items expiring in the next 2 days
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val expiringItems = foodItemDao.getExpiringItems(calendar.time).value ?: emptyList()

            if (expiringItems.isNotEmpty()) {
                sendNotification(expiringItems.size)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification(itemCount: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Food Expiration Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Food Expiring Soon!")
            .setContentText("You have $itemCount items expiring in the next 2 days")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "food_expiration_channel"
        private const val NOTIFICATION_ID = 1
    }
}