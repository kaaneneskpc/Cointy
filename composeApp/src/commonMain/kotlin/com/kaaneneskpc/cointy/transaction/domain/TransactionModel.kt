package com.kaaneneskpc.cointy.transaction.domain

import com.kaaneneskpc.cointy.core.domain.coin.Coin

enum class TransactionType {
    BUY,
    SELL
}

data class TransactionModel(
    val id: Long,
    val type: TransactionType,
    val coin: Coin,
    val amountInFiat: Double,
    val amountInUnit: Double,
    val price: Double,
    val timestamp: Long
)

