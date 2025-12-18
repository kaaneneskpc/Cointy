package com.kaaneneskpc.cointy.alert.domain

class TogglePriceAlertUseCase(
    private val priceAlertRepository: PriceAlertRepository
) {
    suspend fun execute(alertId: Long, isEnabled: Boolean) {
        priceAlertRepository.updateAlertEnabled(alertId, isEnabled)
    }
}

