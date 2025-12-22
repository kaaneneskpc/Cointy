package com.kaaneneskpc.cointy.core.background

import com.kaaneneskpc.cointy.alert.domain.BackgroundCheckPriceAlertsUseCase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval

class IosBackgroundPriceChecker : BackgroundPriceChecker, KoinComponent {
    private val backgroundCheckPriceAlertsUseCase: BackgroundCheckPriceAlertsUseCase by inject()
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun schedulePeriodicPriceCheck() {
        println("IosBackgroundPriceChecker: Scheduling background fetch")
        scheduleBackgroundFetch()
    }
    override fun cancelPeriodicPriceCheck() {
        println("IosBackgroundPriceChecker: Cancelling background fetch")
        BGTaskScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
    }
    @OptIn(ExperimentalForeignApi::class)
    private fun scheduleBackgroundFetch() {
        val request = BGAppRefreshTaskRequest(identifier = TASK_IDENTIFIER)
        request.earliestBeginDate = NSDate().dateByAddingTimeInterval(REFRESH_INTERVAL_SECONDS)
        try {
            BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
            println("IosBackgroundPriceChecker: Background fetch scheduled successfully")
        } catch (e: Exception) {
            println("IosBackgroundPriceChecker: Failed to schedule background fetch: ${e.message}")
        }
    }
    fun handleBackgroundFetch(completionHandler: (Boolean) -> Unit) {
        println("IosBackgroundPriceChecker: Handling background fetch")
        scope.launch {
            try {
                val success = backgroundCheckPriceAlertsUseCase.execute()
                println("IosBackgroundPriceChecker: Background fetch completed with success: $success")
                scheduleBackgroundFetch()
                completionHandler(success)
            } catch (e: Exception) {
                println("IosBackgroundPriceChecker: Background fetch failed: ${e.message}")
                scheduleBackgroundFetch()
                completionHandler(false)
            }
        }
    }
    companion object {
        const val TASK_IDENTIFIER = "com.kaaneneskpc.cointy.pricealert.refresh"
        private const val REFRESH_INTERVAL_SECONDS = 900.0
    }
}

actual fun createBackgroundPriceChecker(): BackgroundPriceChecker {
    return IosBackgroundPriceChecker()
}

