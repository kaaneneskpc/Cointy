package com.kaaneneskpc.cointy.core.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionList
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationResponse
import platform.darwin.NSObject

class IosNotificationService : NotificationService {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private val delegate = NotificationDelegate()
    init {
        notificationCenter.delegate = delegate
    }
    override fun showPriceAlertNotification(
        title: String,
        message: String,
        coinId: String,
        alertId: Long
    ) {
        println("IosNotificationService: Sending notification - $title")
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound)
            setUserInfo(mapOf(
                "coinId" to coinId,
                "alertId" to alertId
            ))
        }
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "price_alert_$alertId",
            content = content,
            trigger = null
        )
        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("IosNotificationService: Failed to show notification: ${error.localizedDescription}")
            } else {
                println("IosNotificationService: Notification sent successfully")
            }
        }
    }
    override fun cancelNotification(notificationId: Int) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf("price_alert_$notificationId")
        )
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(
            listOf("price_alert_$notificationId")
        )
    }
    override fun cancelAllNotifications() {
        notificationCenter.removeAllPendingNotificationRequests()
        notificationCenter.removeAllDeliveredNotifications()
    }
}

private class NotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (ULong) -> Unit
    ) {
        println("NotificationDelegate: willPresentNotification called")
        val options = UNNotificationPresentationOptionBanner or
                UNNotificationPresentationOptionSound or
                UNNotificationPresentationOptionList
        withCompletionHandler(options)
    }
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        println("NotificationDelegate: didReceiveNotificationResponse called")
        withCompletionHandler()
    }
}

