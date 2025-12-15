package com.kaaneneskpc.cointy.trade.mapper

import com.kaaneneskpc.cointy.core.domain.coin.Coin
import com.kaaneneskpc.cointy.trade.presentation.common.UiTradeCoinItem

fun UiTradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)