package com.kaaneneskpc.cointy.alert.data.mapper

import com.kaaneneskpc.cointy.alert.data.local.PriceAlertEntity
import com.kaaneneskpc.cointy.alert.domain.model.AlertCondition
import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel

fun PriceAlertEntity.toDomain(): PriceAlertModel {
    return PriceAlertModel(
        id = id,
        coinId = coinId,
        coinName = coinName,
        coinSymbol = coinSymbol,
        coinIconUrl = coinIconUrl,
        targetPrice = targetPrice,
        condition = AlertCondition.valueOf(condition),
        isEnabled = isEnabled,
        isTriggered = isTriggered,
        createdAt = createdAt,
        triggeredAt = triggeredAt
    )
}

fun PriceAlertModel.toEntity(): PriceAlertEntity {
    return PriceAlertEntity(
        id = id,
        coinId = coinId,
        coinName = coinName,
        coinSymbol = coinSymbol,
        coinIconUrl = coinIconUrl,
        targetPrice = targetPrice,
        condition = condition.name,
        isEnabled = isEnabled,
        isTriggered = isTriggered,
        createdAt = createdAt,
        triggeredAt = triggeredAt
    )
}

