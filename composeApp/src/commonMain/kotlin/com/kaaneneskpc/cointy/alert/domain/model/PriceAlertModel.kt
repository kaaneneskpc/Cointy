package com.kaaneneskpc.cointy.alert.domain.model

data class PriceAlertModel(
    val id: Long = 0,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val targetPrice: Double,
    val condition: AlertCondition,
    val isEnabled: Boolean = true,
    val isTriggered: Boolean = false,
    val createdAt: Long,
    val triggeredAt: Long? = null
)

