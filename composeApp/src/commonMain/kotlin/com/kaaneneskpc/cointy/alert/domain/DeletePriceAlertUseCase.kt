package com.kaaneneskpc.cointy.alert.domain

class DeletePriceAlertUseCase(
    private val priceAlertRepository: PriceAlertRepository
) {
    suspend fun execute(alertId: Long) {
        priceAlertRepository.deleteAlert(alertId)
    }
}

