package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.alert.domain.model.AlertCondition
import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.domain.coin.Coin
import kotlinx.datetime.Clock

class CreatePriceAlertUseCase(
    private val priceAlertRepository: PriceAlertRepository
) {
    suspend fun execute(
        coin: Coin,
        targetPrice: Double,
        condition: AlertCondition
    ): Result<Long, DataError.Local> {
        if (targetPrice <= 0) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
        val alert = PriceAlertModel(
            coinId = coin.id,
            coinName = coin.name,
            coinSymbol = coin.symbol,
            coinIconUrl = coin.iconUrl,
            targetPrice = targetPrice,
            condition = condition,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        val alertId = priceAlertRepository.createAlert(alert)
        return Result.Success(alertId)
    }
}

