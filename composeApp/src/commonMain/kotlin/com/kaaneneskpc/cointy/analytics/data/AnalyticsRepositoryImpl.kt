package com.kaaneneskpc.cointy.analytics.data

import com.kaaneneskpc.cointy.analytics.domain.AnalyticsRepository
import com.kaaneneskpc.cointy.analytics.domain.model.CoinDistribution
import com.kaaneneskpc.cointy.analytics.domain.model.CoinPerformance
import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioAnalytics
import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioHistoryPoint
import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.domain.onError
import com.kaaneneskpc.cointy.core.domain.onSuccess
import com.kaaneneskpc.cointy.portfolio.data.local.PortfolioDao
import com.kaaneneskpc.cointy.transaction.data.local.TransactionDao
import com.kaaneneskpc.cointy.transaction.domain.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class AnalyticsRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val transactionDao: TransactionDao,
    private val coinsRemoteDataSource: CoinRemoteDataSource
) : AnalyticsRepository {

    private val chartColors: List<Long> = listOf(
        0xFF6366F1,
        0xFF8B5CF6,
        0xFF06B6D4,
        0xFF10B981,
        0xFFF59E0B,
        0xFFEF4444,
        0xFFEC4899,
        0xFF14B8A6,
        0xFFF97316,
        0xFF84CC16
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioAnalytics(): Flow<Result<PortfolioAnalytics, DataError.Remote>> {
        return combine(
            portfolioDao.getAllOwnedCoins(),
            transactionDao.getAllTransactions()
        ) { portfolioCoins, transactions ->
            Pair(portfolioCoins, transactions)
        }.flatMapLatest { (portfolioCoins, transactions) ->
            flow {
                if (portfolioCoins.isEmpty()) {
                    emit(Result.Success(createEmptyAnalytics(transactions.size)))
                    return@flow
                }
                coinsRemoteDataSource.getListOfCoins()
                    .onError { error ->
                        emit(Result.Error(error))
                    }
                    .onSuccess { coinsDto ->
                        val totalPortfolioValue = portfolioCoins.sumOf { coin ->
                            val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                            coin.amountOwned * currentPrice
                        }
                        val totalInvestedAmount = portfolioCoins.sumOf { coin ->
                            coin.averagePurchasePrice * coin.amountOwned
                        }
                        val totalProfitLoss = totalPortfolioValue - totalInvestedAmount
                        val profitLossPercentage = if (totalInvestedAmount > 0) {
                            (totalProfitLoss / totalInvestedAmount) * 100
                        } else {
                            0.0
                        }
                        val coinDistributions = portfolioCoins.mapIndexed { index, coin ->
                            val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                            val valueInFiat = coin.amountOwned * currentPrice
                            val percentage = if (totalPortfolioValue > 0) {
                                (valueInFiat / totalPortfolioValue) * 100
                            } else {
                                0.0
                            }
                            CoinDistribution(
                                coinId = coin.coinId,
                                coinName = coin.name,
                                coinSymbol = coin.symbol,
                                coinIconUrl = coin.iconUrl,
                                valueInFiat = valueInFiat,
                                percentage = percentage,
                                color = chartColors[index % chartColors.size]
                            )
                        }.sortedByDescending { it.percentage }
                        val coinPerformances = portfolioCoins.map { coin ->
                            val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                            val currentValue = coin.amountOwned * currentPrice
                            val investedAmount = coin.averagePurchasePrice * coin.amountOwned
                            val profitLoss = currentValue - investedAmount
                            val profitLossPercent = if (investedAmount > 0) {
                                (profitLoss / investedAmount) * 100
                            } else {
                                0.0
                            }
                            CoinPerformance(
                                coinId = coin.coinId,
                                coinName = coin.name,
                                coinSymbol = coin.symbol,
                                coinIconUrl = coin.iconUrl,
                                currentValue = currentValue,
                                investedAmount = investedAmount,
                                profitLoss = profitLoss,
                                profitLossPercentage = profitLossPercent,
                                isPositive = profitLoss >= 0
                            )
                        }.sortedByDescending { kotlin.math.abs(it.profitLossPercentage) }
                        val portfolioHistory = calculatePortfolioHistory(transactions, portfolioCoins.associate { it.coinId to (coinsDto.data.coins.find { c -> c.uuid == it.coinId }?.price ?: 0.0) })
                        val buyCount = transactions.count { it.type == TransactionType.BUY.name }
                        val sellCount = transactions.count { it.type == TransactionType.SELL.name }
                        emit(Result.Success(PortfolioAnalytics(
                            totalPortfolioValue = totalPortfolioValue,
                            totalInvestedAmount = totalInvestedAmount,
                            totalProfitLoss = totalProfitLoss,
                            profitLossPercentage = profitLossPercentage,
                            coinDistributions = coinDistributions,
                            portfolioHistory = portfolioHistory,
                            coinPerformances = coinPerformances,
                            totalTransactionCount = transactions.size,
                            buyTransactionCount = buyCount,
                            sellTransactionCount = sellCount
                        )))
                    }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCoinDistributions(): Flow<Result<List<CoinDistribution>, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoins ->
            flow {
                if (portfolioCoins.isEmpty()) {
                    emit(Result.Success(emptyList<CoinDistribution>()))
                    return@flow
                }
                coinsRemoteDataSource.getListOfCoins()
                    .onError { error ->
                        emit(Result.Error(error))
                    }
                    .onSuccess { coinsDto ->
                        val totalValue = portfolioCoins.sumOf { coin ->
                            val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                            coin.amountOwned * currentPrice
                        }
                        val distributions = portfolioCoins.mapIndexed { index, coin ->
                            val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                            val valueInFiat = coin.amountOwned * currentPrice
                            val percentage = if (totalValue > 0) (valueInFiat / totalValue) * 100 else 0.0
                            CoinDistribution(
                                coinId = coin.coinId,
                                coinName = coin.name,
                                coinSymbol = coin.symbol,
                                coinIconUrl = coin.iconUrl,
                                valueInFiat = valueInFiat,
                                percentage = percentage,
                                color = chartColors[index % chartColors.size]
                            )
                        }.sortedByDescending { it.percentage }
                        emit(Result.Success(distributions))
                    }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCoinPerformances(): Flow<Result<List<CoinPerformance>, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoins ->
            flow {
                if (portfolioCoins.isEmpty()) {
                    emit(Result.Success(emptyList<CoinPerformance>()))
                    return@flow
                }
                coinsRemoteDataSource.getListOfCoins()
                    .onError { error ->
                        emit(Result.Error(error))
                    }
                    .onSuccess { coinsDto ->
                        val performances = portfolioCoins.map { coin ->
                            val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                            val currentValue = coin.amountOwned * currentPrice
                            val investedAmount = coin.averagePurchasePrice * coin.amountOwned
                            val profitLoss = currentValue - investedAmount
                            val profitLossPercent = if (investedAmount > 0) (profitLoss / investedAmount) * 100 else 0.0
                            CoinPerformance(
                                coinId = coin.coinId,
                                coinName = coin.name,
                                coinSymbol = coin.symbol,
                                coinIconUrl = coin.iconUrl,
                                currentValue = currentValue,
                                investedAmount = investedAmount,
                                profitLoss = profitLoss,
                                profitLossPercentage = profitLossPercent,
                                isPositive = profitLoss >= 0
                            )
                        }.sortedByDescending { kotlin.math.abs(it.profitLossPercentage) }
                        emit(Result.Success(performances))
                    }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioHistory(): Flow<Result<List<PortfolioHistoryPoint>, DataError.Remote>> {
        return transactionDao.getAllTransactions().flatMapLatest { transactions ->
            flow<Result<List<PortfolioHistoryPoint>, DataError.Remote>> {
                if (transactions.isEmpty()) {
                    emit(Result.Success(emptyList()))
                    return@flow
                }
                val sortedTransactions = transactions.sortedBy { it.timestamp }
                var runningValue = 0.0
                val historyPoints = sortedTransactions.map { transaction ->
                    if (transaction.type == TransactionType.BUY.name) {
                        runningValue += transaction.amountInFiat
                    } else {
                        runningValue -= transaction.amountInFiat
                    }
                    PortfolioHistoryPoint(
                        timestamp = transaction.timestamp,
                        totalValue = runningValue.coerceAtLeast(0.0)
                    )
                }
                emit(Result.Success(historyPoints))
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    override suspend fun calculateTotalInvestedAmount(): Double {
        val portfolioCoins = portfolioDao.getAllOwnedCoinsList()
        return portfolioCoins.sumOf { it.averagePurchasePrice * it.amountOwned }
    }

    override suspend fun calculateTotalProfitLoss(): Result<Double, DataError.Remote> {
        val portfolioCoins = portfolioDao.getAllOwnedCoinsList()
        if (portfolioCoins.isEmpty()) {
            return Result.Success(0.0)
        }
        coinsRemoteDataSource.getListOfCoins()
            .onError { error ->
                return Result.Error(error)
            }
            .onSuccess { coinsDto ->
                val totalCurrentValue = portfolioCoins.sumOf { coin ->
                    val currentPrice = coinsDto.data.coins.find { it.uuid == coin.coinId }?.price ?: 0.0
                    coin.amountOwned * currentPrice
                }
                val totalInvested = portfolioCoins.sumOf { it.averagePurchasePrice * it.amountOwned }
                return Result.Success(totalCurrentValue - totalInvested)
            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    private fun createEmptyAnalytics(transactionCount: Int): PortfolioAnalytics {
        return PortfolioAnalytics(
            totalPortfolioValue = 0.0,
            totalInvestedAmount = 0.0,
            totalProfitLoss = 0.0,
            profitLossPercentage = 0.0,
            coinDistributions = emptyList(),
            portfolioHistory = emptyList(),
            coinPerformances = emptyList(),
            totalTransactionCount = transactionCount,
            buyTransactionCount = 0,
            sellTransactionCount = 0
        )
    }

    private fun calculatePortfolioHistory(
        transactions: List<com.kaaneneskpc.cointy.transaction.data.local.TransactionEntity>,
        currentPrices: Map<String, Double>
    ): List<PortfolioHistoryPoint> {
        if (transactions.isEmpty()) return emptyList()
        val sortedTransactions = transactions.sortedBy { it.timestamp }
        val holdings = mutableMapOf<String, Double>()
        return sortedTransactions.map { transaction ->
            val currentAmount = holdings[transaction.coinId] ?: 0.0
            if (transaction.type == TransactionType.BUY.name) {
                holdings[transaction.coinId] = currentAmount + transaction.amountInUnit
            } else {
                holdings[transaction.coinId] = (currentAmount - transaction.amountInUnit).coerceAtLeast(0.0)
            }
            val totalValue = holdings.entries.sumOf { (coinId, amount) ->
                val price = currentPrices[coinId] ?: transaction.price
                amount * price
            }
            PortfolioHistoryPoint(
                timestamp = transaction.timestamp,
                totalValue = totalValue
            )
        }
    }
}

