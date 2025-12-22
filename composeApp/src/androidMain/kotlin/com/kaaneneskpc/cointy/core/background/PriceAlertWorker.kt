package com.kaaneneskpc.cointy.core.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaaneneskpc.cointy.alert.domain.BackgroundCheckPriceAlertsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PriceAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {
    private val backgroundCheckPriceAlertsUseCase: BackgroundCheckPriceAlertsUseCase by inject()
    override suspend fun doWork(): Result {
        Log.d(TAG, "PriceAlertWorker started")
        return try {
            val success = backgroundCheckPriceAlertsUseCase.execute()
            if (success) {
                Log.d(TAG, "PriceAlertWorker completed successfully")
                Result.success()
            } else {
                Log.w(TAG, "PriceAlertWorker failed to fetch coin prices")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "PriceAlertWorker failed with exception", e)
            Result.retry()
        }
    }
    companion object {
        private const val TAG = "PriceAlertWorker"
        const val WORK_NAME = "price_alert_periodic_work"
    }
}

