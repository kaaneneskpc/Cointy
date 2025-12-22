package com.kaaneneskpc.cointy.core.background

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.BackgroundTasks.BGAppRefreshTask
import platform.BackgroundTasks.BGTaskScheduler

object IosBackgroundTaskHandler : KoinComponent {
    private val backgroundPriceChecker: BackgroundPriceChecker by inject()
    private var isRegistered: Boolean = false
    fun registerBackgroundTasks() {
        if (isRegistered) {
            println("IosBackgroundTaskHandler: Background tasks already registered, skipping")
            return
        }
        println("IosBackgroundTaskHandler: Registering background tasks")
        BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
            identifier = IosBackgroundPriceChecker.TASK_IDENTIFIER,
            usingQueue = null
        ) { task ->
            handleAppRefresh(task as BGAppRefreshTask)
        }
        isRegistered = true
        println("IosBackgroundTaskHandler: Background tasks registered")
    }
    private fun handleAppRefresh(task: BGAppRefreshTask) {
        println("IosBackgroundTaskHandler: Handling app refresh task")
        task.expirationHandler = {
            println("IosBackgroundTaskHandler: Task expired")
            task.setTaskCompletedWithSuccess(false)
        }
        (backgroundPriceChecker as? IosBackgroundPriceChecker)?.handleBackgroundFetch { success ->
            println("IosBackgroundTaskHandler: Task completed with success: $success")
            task.setTaskCompletedWithSuccess(success)
        }
    }
    fun scheduleBackgroundTasks() {
        println("IosBackgroundTaskHandler: Scheduling background tasks")
        backgroundPriceChecker.schedulePeriodicPriceCheck()
    }
}
