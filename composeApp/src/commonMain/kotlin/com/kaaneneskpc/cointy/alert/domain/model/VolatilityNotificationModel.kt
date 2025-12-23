package com.kaaneneskpc.cointy.alert.domain.model

data class VolatilityNotificationModel(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val priceChangePercent: Double,
    val currentPrice: Double,
    val isPositive: Boolean,
    val timestamp: Long
)
