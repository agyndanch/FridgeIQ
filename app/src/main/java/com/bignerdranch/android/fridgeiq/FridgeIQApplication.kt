package com.bignerdranch.android.fridgeiq

import android.app.Application
import androidx.work.*
import com.bignerdranch.android.fridgeiq.worker.ExpirationNotificationWorker
import com.bignerdranch.android.fridgeiq.data.database.FridgeIQDatabase
import com.bignerdranch.android.fridgeiq.data.repository.FridgeIQRepository
import java.util.concurrent.TimeUnit

class FridgeIQApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize repository
        val database = FridgeIQDatabase.getDatabase(this)
        FridgeIQRepository.initialize(
            database.foodItemDao(),
            database.wasteEntryDao(),
            database.shoppingListDao()
        )

        // Schedule daily notification check
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<ExpirationNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "expiration_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }
}