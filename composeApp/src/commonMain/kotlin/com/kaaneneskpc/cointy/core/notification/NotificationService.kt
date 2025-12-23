package com.kaaneneskpc.cointy.core.notification

interface NotificationService {
    fun showPriceAlertNotification(
        title: String,
        message: String,
        coinId: String,
        alertId: Long
    )
    fun showVolatilityNotification(
        title: String,
        message: String,
        coinId: String,
        changePercent: Double
    )
    fun cancelNotification(notificationId: Int)
    fun cancelAllNotifications()
}

