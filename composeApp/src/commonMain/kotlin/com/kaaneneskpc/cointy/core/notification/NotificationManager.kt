package com.kaaneneskpc.cointy.core.notification

expect class NotificationManager {
    fun initialize()
    fun requestPermission(onResult: (Boolean) -> Unit)
    fun hasPermission(): Boolean
}

