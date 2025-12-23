package com.kaaneneskpc.cointy.coins.data.mapper

import com.kaaneneskpc.cointy.core.domain.coin.Coin
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinItem
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinPrice
import com.kaaneneskpc.cointy.coins.domain.model.CoinModel
import com.kaaneneskpc.cointy.coins.domain.model.PriceModel

fun CoinItem.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price,
    change = change,
)

fun CoinPrice.toPriceModel() = PriceModel(
    price = price?.toDoubleOrNull() ?: 0.0,
    timestamp = timestamp,
)