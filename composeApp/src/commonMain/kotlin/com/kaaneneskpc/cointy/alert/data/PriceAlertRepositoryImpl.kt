package com.kaaneneskpc.cointy.alert.data

import com.kaaneneskpc.cointy.alert.data.local.PriceAlertDao
import com.kaaneneskpc.cointy.alert.data.mapper.toDomain
import com.kaaneneskpc.cointy.alert.data.mapper.toEntity
import com.kaaneneskpc.cointy.alert.domain.PriceAlertRepository
import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PriceAlertRepositoryImpl(
    private val priceAlertDao: PriceAlertDao
) : PriceAlertRepository {
    override fun getAllAlerts(): Flow<List<PriceAlertModel>> {
        return priceAlertDao.getAllAlerts().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    override fun getActiveAlerts(): Flow<List<PriceAlertModel>> {
        return priceAlertDao.getActiveAlerts().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    override fun getAlertsByCoinId(coinId: String): Flow<List<PriceAlertModel>> {
        return priceAlertDao.getAlertsByCoinId(coinId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    override suspend fun getAlertById(alertId: Long): PriceAlertModel? {
        return priceAlertDao.getAlertById(alertId)?.toDomain()
    }
    override suspend fun createAlert(alert: PriceAlertModel): Long {
        return priceAlertDao.insertAlert(alert.toEntity())
    }
    override suspend fun updateAlert(alert: PriceAlertModel) {
        priceAlertDao.updateAlert(alert.toEntity())
    }
    override suspend fun deleteAlert(alertId: Long) {
        priceAlertDao.deleteAlertById(alertId)
    }
    override suspend fun markAlertAsTriggered(alertId: Long, triggeredAt: Long) {
        priceAlertDao.markAlertAsTriggered(alertId, triggeredAt)
    }
    override suspend fun updateAlertEnabled(alertId: Long, isEnabled: Boolean) {
        priceAlertDao.updateAlertEnabled(alertId, isEnabled)
    }
}

