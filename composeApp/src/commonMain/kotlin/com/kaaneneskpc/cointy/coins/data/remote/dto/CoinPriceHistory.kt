package com.kaaneneskpc.cointy.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceHistoryResponse(
    val data: CoinPriceHistory
)

@Serializable
data class CoinPriceHistory(
    val history: List<CoinPrice>
)

@Serializable
data class CoinPrice(
    val price: String?,
    val timestamp: Long
)



