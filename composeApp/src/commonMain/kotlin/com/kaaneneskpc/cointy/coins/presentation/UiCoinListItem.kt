package com.kaaneneskpc.cointy.coins.presentation

data class UiCoinListItem(
    val id: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val formattedPrice: String,
    val formattedChange: String,
    val isPositive: Boolean,
    val price: Double = 0.0,
    val change: Double = 0.0
)
