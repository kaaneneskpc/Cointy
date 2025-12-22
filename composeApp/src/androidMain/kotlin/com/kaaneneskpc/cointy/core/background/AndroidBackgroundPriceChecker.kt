package com.kaaneneskpc.cointy.core.background

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AndroidBackgroundPriceChecker(
    private val context: Context
) : BackgroundPriceChecker {
    override fun schedulePeriodicPriceCheck() {
        Log.d(TAG, "Scheduling periodic price check")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<PriceAlertWorker>(
            REPEAT_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_DELAY_MINUTES,
                TimeUnit.MINUTES
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PriceAlertWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        Log.d(TAG, "Periodic price check scheduled")
    }
    override fun cancelPeriodicPriceCheck() {
        Log.d(TAG, "Cancelling periodic price check")
        WorkManager.getInstance(context).cancelUniqueWork(PriceAlertWorker.WORK_NAME)
    }
    companion object {
        private const val TAG = "AndroidBackgroundPriceChecker"
        private const val REPEAT_INTERVAL_MINUTES = 15L
        private const val BACKOFF_DELAY_MINUTES = 5L
    }
}

actual fun createBackgroundPriceChecker(): BackgroundPriceChecker {
    throw IllegalStateException("Use createBackgroundPriceChecker(context) on Android")
}

fun createBackgroundPriceChecker(context: Context): BackgroundPriceChecker {
    return AndroidBackgroundPriceChecker(context)
}

