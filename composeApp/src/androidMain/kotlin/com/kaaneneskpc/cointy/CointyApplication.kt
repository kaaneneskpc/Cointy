package com.kaaneneskpc.cointy

import android.app.Application
import com.kaaneneskpc.cointy.core.notification.NotificationManager
import com.kaaneneskpc.cointy.di.initKoin
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class CointyApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CointyApplication)
        }
        initializeNotifications()
    }
    private fun initializeNotifications() {
        val notificationManager: NotificationManager by inject()
        notificationManager.initialize()
    }
}