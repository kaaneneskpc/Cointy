package com.kaaneneskpc.cointy.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailResponse(
    val data: CoinsResponse
)

@Serializable
data class CoinsResponse(
    val coin: CoinItem
)
