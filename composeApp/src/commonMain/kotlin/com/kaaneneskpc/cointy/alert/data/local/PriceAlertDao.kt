package com.kaaneneskpc.cointy.alert.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity): Long
    @Update
    suspend fun updateAlert(alert: PriceAlertEntity)
    @Delete
    suspend fun deleteAlert(alert: PriceAlertEntity)
    @Query("DELETE FROM PriceAlertEntity WHERE id = :alertId")
    suspend fun deleteAlertById(alertId: Long)
    @Query("SELECT * FROM PriceAlertEntity ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlertEntity>>
    @Query("SELECT * FROM PriceAlertEntity WHERE isEnabled = 1 AND isTriggered = 0")
    fun getActiveAlerts(): Flow<List<PriceAlertEntity>>
    @Query("SELECT * FROM PriceAlertEntity WHERE coinId = :coinId ORDER BY createdAt DESC")
    fun getAlertsByCoinId(coinId: String): Flow<List<PriceAlertEntity>>
    @Query("SELECT * FROM PriceAlertEntity WHERE id = :alertId")
    suspend fun getAlertById(alertId: Long): PriceAlertEntity?
    @Query("UPDATE PriceAlertEntity SET isTriggered = 1, triggeredAt = :triggeredAt WHERE id = :alertId")
    suspend fun markAlertAsTriggered(alertId: Long, triggeredAt: Long)
    @Query("UPDATE PriceAlertEntity SET isEnabled = :isEnabled WHERE id = :alertId")
    suspend fun updateAlertEnabled(alertId: Long, isEnabled: Boolean)
}

