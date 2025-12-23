package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.alert.data.VolatilityNotificationTracker
import com.kaaneneskpc.cointy.core.notification.NotificationService
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.core.util.formatCoinPriceUnit
import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlin.math.abs

data class CoinVolatilityData(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val currentPrice: Double,
    val priceChangePercent24h: Double
)

class CheckVolatilityAlertsUseCase(
    private val settingsRepository: SettingsRepository,
    private val notificationService: NotificationService,
    private val volatilityNotificationTracker: VolatilityNotificationTracker
) {
    suspend fun execute(coins: List<CoinVolatilityData>) {
        val isEnabled = settingsRepository.getVolatilityAlertsEnabled().first()
        val isNotificationsEnabled = settingsRepository.getNotificationsEnabled().first()
        if (!isEnabled || !isNotificationsEnabled) return
        val threshold = settingsRepository.getVolatilityThreshold().first()
        coins.forEach { coin ->
            val absChangePercent = abs(coin.priceChangePercent24h)
            if (absChangePercent >= threshold && volatilityNotificationTracker.shouldNotify(coin.coinId)) {
                sendVolatilityNotification(coin)
                volatilityNotificationTracker.markAsNotified(coin.coinId)
            }
        }
    }
    private fun sendVolatilityNotification(coin: CoinVolatilityData) {
        val isPositive = coin.priceChangePercent24h >= 0
        val direction = if (isPositive) "rose" else "dropped"
        val changeFormatted = formatCoinPricePercentage(abs(coin.priceChangePercent24h))
        val title = "${coin.coinSymbol} Price Alert"
        val message = "${coin.coinName} $direction $changeFormatted% in the last 24h. Current price: ${formatCoinPrice(coin.currentPrice)}"
        notificationService.showVolatilityNotification(
            title = title,
            message = message,
            coinId = coin.coinId,
            changePercent = coin.priceChangePercent24h
        )
    }
}
