package com.kaaneneskpc.cointy.alert.domain

import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.core.domain.Result

class BackgroundCheckPriceAlertsUseCase(
    private val coinRemoteDataSource: CoinRemoteDataSource,
    private val checkPriceAlertsUseCase: CheckPriceAlertsUseCase
) {
    suspend fun execute(): Boolean {
        return when (val result = coinRemoteDataSource.getListOfCoins()) {
            is Result.Success -> {
                val coinPrices = result.data.data.coins.associate { coin ->
                    coin.uuid to coin.price
                }
                checkPriceAlertsUseCase.execute(coinPrices)
                true
            }
            is Result.Error -> {
                false
            }
        }
    }
}

