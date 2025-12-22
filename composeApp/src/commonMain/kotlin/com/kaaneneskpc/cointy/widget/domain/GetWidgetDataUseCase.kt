package com.kaaneneskpc.cointy.widget.domain

import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.domain.onError
import com.kaaneneskpc.cointy.core.domain.onSuccess
import com.kaaneneskpc.cointy.portfolio.data.local.PortfolioDao
import com.kaaneneskpc.cointy.portfolio.data.local.UserBalanceDao
import kotlinx.datetime.Clock

class GetWidgetDataUseCase(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinRemoteDataSource: CoinRemoteDataSource
) {
    suspend fun execute(): Result<WidgetData, DataError.Remote> {
        val portfolioCoins = portfolioDao.getAllOwnedCoinsList()
        val cashBalance = userBalanceDao.getCashBalance() ?: 10000.0
        coinRemoteDataSource.getListOfCoins()
            .onError { error ->
                return Result.Error(error)
            }
            .onSuccess { coinsResponse ->
                val coinPriceMap = coinsResponse.data.coins.associateBy { it.uuid }
                var totalPortfolioValue = 0.0
                val coinWidgetDataList = portfolioCoins.mapNotNull { portfolioCoin ->
                    val remoteCoin = coinPriceMap[portfolioCoin.coinId]
                    remoteCoin?.let { coin ->
                        val coinValue = portfolioCoin.amountOwned * coin.price
                        totalPortfolioValue += coinValue
                        CoinWidgetData(
                            coinId = portfolioCoin.coinId,
                            name = portfolioCoin.name,
                            symbol = portfolioCoin.symbol,
                            iconUrl = portfolioCoin.iconUrl,
                            price = coin.price,
                            change24h = coin.change
                        )
                    }
                }
                val portfolioWidgetData = PortfolioWidgetData(
                    totalPortfolioValue = totalPortfolioValue,
                    cashBalance = cashBalance,
                    lastUpdated = Clock.System.now().toEpochMilliseconds()
                )
                return Result.Success(
                    WidgetData(
                        portfolioData = portfolioWidgetData,
                        coins = coinWidgetDataList
                    )
                )
            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }
}
