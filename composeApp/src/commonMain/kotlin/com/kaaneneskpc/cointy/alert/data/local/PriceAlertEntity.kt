package com.kaaneneskpc.cointy.alert.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PriceAlertEntity")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val targetPrice: Double,
    val condition: String,
    val isEnabled: Boolean = true,
    val isTriggered: Boolean = false,
    val createdAt: Long,
    val triggeredAt: Long? = null
)

