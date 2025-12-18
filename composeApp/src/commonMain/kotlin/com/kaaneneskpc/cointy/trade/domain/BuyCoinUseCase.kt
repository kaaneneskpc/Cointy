package com.kaaneneskpc.cointy.trade.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.EmptyResult
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.domain.coin.Coin
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioCoinModel
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioRepository
import com.kaaneneskpc.cointy.transaction.domain.TransactionModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionRepository
import com.kaaneneskpc.cointy.transaction.domain.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun buyCoin(
        coin: Coin,
        amountInPrice: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.cashBalanceFlow().first()
        if (balance < amountInPrice) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }
        val existingCoinResult = portfolioRepository.getPortfolioCoin(coin.id)
        val existingCoin = when (existingCoinResult) {
            is Result.Success -> existingCoinResult.data
            is Result.Error -> return Result.Error(existingCoinResult.error)
        }
        val amountInUnit = amountInPrice / price
        existingCoin?.let {
            val newAmountOwned = existingCoin.ownedAmountInUnit + amountInUnit
            val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInPrice
            val newAveragePurchasePrice = newTotalInvestment / newAmountOwned
            portfolioRepository.savePortfolioCoin(
                existingCoin.copy(
                    ownedAmountInUnit = newAmountOwned,
                    ownedAmountInFiat = newTotalInvestment,
                    averagePurchasePrice = newAveragePurchasePrice
                )
            )
        } ?: run {
            portfolioRepository.savePortfolioCoin(
                PortfolioCoinModel(
                    coin = coin,
                    performancePercent = 0.0,
                    averagePurchasePrice = price,
                    ownedAmountInUnit = amountInUnit,
                    ownedAmountInFiat = amountInPrice
                )
            )
        }

        portfolioRepository.updateCashBalance(balance - amountInPrice)
        
        val transaction = TransactionModel(
            id = 0,
            type = TransactionType.BUY,
            coin = coin,
            amountInFiat = amountInPrice,
            amountInUnit = amountInUnit,
            price = price,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        transactionRepository.saveTransaction(transaction)
        
        return Result.Success(Unit)

    }
}