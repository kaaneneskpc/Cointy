package com.kaaneneskpc.cointy.trade.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.EmptyResult
import com.kaaneneskpc.cointy.core.domain.coin.Coin
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioRepository
import com.kaaneneskpc.cointy.transaction.domain.TransactionModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionRepository
import com.kaaneneskpc.cointy.transaction.domain.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend fun sellCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val sellAllThreshold = 1
        when(val existingCoinResponse = portfolioRepository.getPortfolioCoin(coin.id)) {
            is Result.Success -> {
                val existingCoin = existingCoinResponse.data
                val sellAmountInUnit = amountInFiat / price

                val balance = portfolioRepository.cashBalanceFlow().first()
                if (existingCoin == null || existingCoin.ownedAmountInUnit < sellAmountInUnit) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }
                val remainingAmountFiat = existingCoin.ownedAmountInFiat - amountInFiat
                val remainingAmountUnit = existingCoin.ownedAmountInUnit - sellAmountInUnit
                if (remainingAmountFiat < sellAllThreshold) {
                    portfolioRepository.removeCoinFromPortfolio(coin.id)
                } else {
                    portfolioRepository.savePortfolioCoin(
                        existingCoin.copy(
                            ownedAmountInUnit = remainingAmountUnit,
                            ownedAmountInFiat = remainingAmountFiat,
                        )
                    )
                }
                portfolioRepository.updateCashBalance(balance + amountInFiat)
                
                val transaction = TransactionModel(
                    id = 0,
                    type = TransactionType.SELL,
                    coin = coin,
                    amountInFiat = amountInFiat,
                    amountInUnit = sellAmountInUnit,
                    price = price,
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
                transactionRepository.saveTransaction(transaction)
                
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return existingCoinResponse
            }
        }
    }
}