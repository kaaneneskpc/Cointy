package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel
import kotlinx.coroutines.flow.Flow

class GetPriceAlertsUseCase(
    private val priceAlertRepository: PriceAlertRepository
) {
    fun executeAll(): Flow<List<PriceAlertModel>> {
        return priceAlertRepository.getAllAlerts()
    }
    fun executeActive(): Flow<List<PriceAlertModel>> {
        return priceAlertRepository.getActiveAlerts()
    }
    fun executeByCoinId(coinId: String): Flow<List<PriceAlertModel>> {
        return priceAlertRepository.getAlertsByCoinId(coinId)
    }
}

