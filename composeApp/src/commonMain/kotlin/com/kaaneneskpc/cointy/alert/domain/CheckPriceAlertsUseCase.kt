package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.alert.domain.model.AlertCondition
import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel
import com.kaaneneskpc.cointy.core.notification.NotificationService
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class CheckPriceAlertsUseCase(
    private val priceAlertRepository: PriceAlertRepository,
    private val notificationService: NotificationService
) {
    suspend fun execute(coinPrices: Map<String, Double>) {
        val activeAlerts = priceAlertRepository.getActiveAlerts().first()
        activeAlerts.forEach { alert ->
            val currentPrice = coinPrices[alert.coinId] ?: return@forEach
            if (shouldTriggerAlert(alert, currentPrice)) {
                triggerAlert(alert, currentPrice)
            }
        }
    }
    private fun shouldTriggerAlert(alert: PriceAlertModel, currentPrice: Double): Boolean {
        return when (alert.condition) {
            AlertCondition.ABOVE -> currentPrice >= alert.targetPrice
            AlertCondition.BELOW -> currentPrice <= alert.targetPrice
        }
    }
    private suspend fun triggerAlert(alert: PriceAlertModel, currentPrice: Double) {
        val triggeredAt = Clock.System.now().toEpochMilliseconds()
        priceAlertRepository.markAlertAsTriggered(alert.id, triggeredAt)
        val conditionText = when (alert.condition) {
            AlertCondition.ABOVE -> "above"
            AlertCondition.BELOW -> "below"
        }
        val title = "${alert.coinSymbol} Price Alert"
        val message = "${alert.coinName} is now $conditionText ${formatCoinPrice(alert.targetPrice)}. Current price: ${formatCoinPrice(currentPrice)}"
        notificationService.showPriceAlertNotification(
            title = title,
            message = message,
            coinId = alert.coinId,
            alertId = alert.id
        )
    }
}

