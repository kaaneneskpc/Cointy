package com.kaaneneskpc.cointy

import androidx.compose.ui.window.ComposeUIViewController
import com.kaaneneskpc.cointy.core.background.IosBackgroundTaskHandler
import com.kaaneneskpc.cointy.core.notification.NotificationManager
import com.kaaneneskpc.cointy.di.initKoin
import com.kaaneneskpc.cointy.widget.IosWidgetRefresher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object IosNotificationInitializer : KoinComponent {
    private val notificationManager: NotificationManager by inject()
    fun initialize() {
        notificationManager.initialize()
    }
}

object IosBackgroundInitializer {
    fun scheduleBackgroundTasks() {
        IosBackgroundTaskHandler.scheduleBackgroundTasks()
    }
}

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        IosNotificationInitializer.initialize()
        IosBackgroundInitializer.scheduleBackgroundTasks()
        IosWidgetRefresher.refreshWidgetData()
    }
) { App() }