package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.core.domain.Result

class BackgroundCheckPriceAlertsUseCase(
    private val coinRemoteDataSource: CoinRemoteDataSource,
    private val checkPriceAlertsUseCase: CheckPriceAlertsUseCase,
    private val checkVolatilityAlertsUseCase: CheckVolatilityAlertsUseCase
) {
    suspend fun execute(): Boolean {
        return when (val result = coinRemoteDataSource.getListOfCoins()) {
            is Result.Success -> {
                val coins = result.data.data.coins
                val coinPrices = coins.associate { coin ->
                    coin.uuid to coin.price
                }
                checkPriceAlertsUseCase.execute(coinPrices)
                val volatilityData = coins.map { coin ->
                    CoinVolatilityData(
                        coinId = coin.uuid,
                        coinName = coin.name,
                        coinSymbol = coin.symbol,
                        coinIconUrl = coin.iconUrl ?: "",
                        currentPrice = coin.price,
                        priceChangePercent24h = coin.change
                    )
                }
                checkVolatilityAlertsUseCase.execute(volatilityData)
                true
            }
            is Result.Error -> {
                false
            }
        }
    }
}

