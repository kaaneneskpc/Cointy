package com.kaaneneskpc.cointy.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinResponse(
    val data: CoinList
)

@Serializable
data class CoinList(
    val coins: List<CoinItem>
)

@Serializable
data class CoinItem(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val price: Double,
    val rank: Int,
    val change: Double,
)