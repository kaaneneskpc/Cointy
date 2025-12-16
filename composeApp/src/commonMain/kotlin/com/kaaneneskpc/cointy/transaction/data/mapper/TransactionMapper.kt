package com.kaaneneskpc.cointy.transaction.data.mapper

import com.kaaneneskpc.cointy.core.domain.coin.Coin
import com.kaaneneskpc.cointy.transaction.data.local.TransactionEntity
import com.kaaneneskpc.cointy.transaction.domain.TransactionModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionType

fun TransactionEntity.toTransactionModel(): TransactionModel {
    return TransactionModel(
        id = id,
        type = when (type) {
            "BUY" -> TransactionType.BUY
            "SELL" -> TransactionType.SELL
            else -> throw IllegalArgumentException("Unknown transaction type: $type")
        },
        coin = Coin(
            id = coinId,
            name = coinName,
            symbol = coinSymbol,
            iconUrl = coinIconUrl
        ),
        amountInFiat = amountInFiat,
        amountInUnit = amountInUnit,
        price = price,
        timestamp = timestamp
    )
}

fun TransactionModel.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        type = when (type) {
            TransactionType.BUY -> "BUY"
            TransactionType.SELL -> "SELL"
        },
        coinId = coin.id,
        coinName = coin.name,
        coinSymbol = coin.symbol,
        coinIconUrl = coin.iconUrl,
        amountInFiat = amountInFiat,
        amountInUnit = amountInUnit,
        price = price,
        timestamp = timestamp
    )
}

