package com.kaaneneskpc.cointy.core.notification

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

actual class NotificationManager {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    actual fun initialize() {
        requestPermission { }
    }
    actual fun requestPermission(onResult: (Boolean) -> Unit) {
        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        notificationCenter.requestAuthorizationWithOptions(options) { granted, _ ->
            onResult(granted)
        }
    }
    actual fun hasPermission(): Boolean {
        var hasPermission = false
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            hasPermission = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
        }
        return hasPermission
    }
}

