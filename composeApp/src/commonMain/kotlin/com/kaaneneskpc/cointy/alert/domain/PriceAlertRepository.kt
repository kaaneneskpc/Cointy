package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel
import kotlinx.coroutines.flow.Flow

interface PriceAlertRepository {
    fun getAllAlerts(): Flow<List<PriceAlertModel>>
    fun getActiveAlerts(): Flow<List<PriceAlertModel>>
    fun getAlertsByCoinId(coinId: String): Flow<List<PriceAlertModel>>
    suspend fun getAlertById(alertId: Long): PriceAlertModel?
    suspend fun createAlert(alert: PriceAlertModel): Long
    suspend fun updateAlert(alert: PriceAlertModel)
    suspend fun deleteAlert(alertId: Long)
    suspend fun markAlertAsTriggered(alertId: Long, triggeredAt: Long)
    suspend fun updateAlertEnabled(alertId: Long, isEnabled: Boolean)
}

