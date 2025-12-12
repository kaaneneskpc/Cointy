package com.kaaneneskpc.cointy.coins.domain.model

import com.kaaneneskpc.cointy.core.domain.coin.Coin

data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double,
)