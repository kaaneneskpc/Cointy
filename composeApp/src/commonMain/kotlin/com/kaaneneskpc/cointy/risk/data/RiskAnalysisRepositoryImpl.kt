package com.kaaneneskpc.cointy.risk.data

import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.domain.onError
import com.kaaneneskpc.cointy.core.domain.onSuccess
import com.kaaneneskpc.cointy.portfolio.data.local.PortfolioDao
import com.kaaneneskpc.cointy.risk.domain.RiskAnalysisRepository
import com.kaaneneskpc.cointy.risk.domain.model.CoinRiskMetrics
import com.kaaneneskpc.cointy.risk.domain.model.PortfolioRiskAnalysis
import com.kaaneneskpc.cointy.risk.domain.model.RiskLevel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

class RiskAnalysisRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val coinRemoteDataSource: CoinRemoteDataSource
) : RiskAnalysisRepository {

    companion object {
        private const val VOLATILITY_WEIGHT = 0.50
        private const val DIVERSIFICATION_WEIGHT = 0.30
        private const val CONCENTRATION_WEIGHT = 0.20
        private const val MAX_DIVERSIFICATION_COINS = 10
        private const val LOW_RISK_THRESHOLD = 30.0
        private const val MODERATE_RISK_THRESHOLD = 50.0
        private const val HIGH_RISK_THRESHOLD = 70.0
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioRiskAnalysis(): Flow<Result<PortfolioRiskAnalysis, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoins ->
            flow {
                if (portfolioCoins.isEmpty()) {
                    emit(Result.Success(createEmptyRiskAnalysis()))
                    return@flow
                }
                coinRemoteDataSource.getListOfCoins()
                    .onError { error ->
                        emit(Result.Error(error))
                    }
                    .onSuccess { coinsResponse ->
                        val totalPortfolioValue = calculateTotalPortfolioValue(
                            portfolioCoins,
                            coinsResponse.data.coins
                        )
                        if (totalPortfolioValue <= 0) {
                            emit(Result.Success(createEmptyRiskAnalysis()))
                            return@onSuccess
                        }
                        val coinRiskMetrics = calculateCoinRiskMetrics(
                            portfolioCoins,
                            coinsResponse.data.coins,
                            totalPortfolioValue
                        )
                        val portfolioVolatility = calculatePortfolioVolatility(coinRiskMetrics)
                        val diversificationScore = calculateDiversificationScore(coinRiskMetrics.size)
                        val concentrationRisk = calculateConcentrationRisk(coinRiskMetrics)
                        val riskScore = calculateRiskScore(
                            portfolioVolatility,
                            diversificationScore,
                            concentrationRisk
                        )
                        val riskLevel = determineRiskLevel(riskScore)
                        emit(Result.Success(
                            PortfolioRiskAnalysis(
                                portfolioVolatility = portfolioVolatility,
                                riskScore = riskScore,
                                riskLevel = riskLevel,
                                diversificationScore = diversificationScore,
                                concentrationRisk = concentrationRisk,
                                coinRiskMetrics = coinRiskMetrics.sortedByDescending { it.contributionToRisk }
                            )
                        ))
                    }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    private fun calculateTotalPortfolioValue(
        portfolioCoins: List<com.kaaneneskpc.cointy.portfolio.data.local.PortfolioCoinEntity>,
        remoteCoins: List<com.kaaneneskpc.cointy.coins.data.remote.dto.CoinItem>
    ): Double {
        return portfolioCoins.sumOf { coin ->
            val currentPrice = remoteCoins.find { it.uuid == coin.coinId }?.price ?: 0.0
            coin.amountOwned * currentPrice
        }
    }

    private fun calculateCoinRiskMetrics(
        portfolioCoins: List<com.kaaneneskpc.cointy.portfolio.data.local.PortfolioCoinEntity>,
        remoteCoins: List<com.kaaneneskpc.cointy.coins.data.remote.dto.CoinItem>,
        totalPortfolioValue: Double
    ): List<CoinRiskMetrics> {
        return portfolioCoins.mapNotNull { coin ->
            val remoteCoin = remoteCoins.find { it.uuid == coin.coinId } ?: return@mapNotNull null
            val currentPrice = remoteCoin.price
            val valueInFiat = coin.amountOwned * currentPrice
            val allocationPercentage = (valueInFiat / totalPortfolioValue) * 100
            val volatility = abs(remoteCoin.change)
            val contributionToRisk = (allocationPercentage / 100) * volatility
            CoinRiskMetrics(
                coinId = coin.coinId,
                coinName = coin.name,
                coinSymbol = coin.symbol,
                coinIconUrl = coin.iconUrl,
                volatility = volatility,
                allocationPercentage = allocationPercentage,
                contributionToRisk = contributionToRisk
            )
        }
    }

    private fun calculatePortfolioVolatility(coinRiskMetrics: List<CoinRiskMetrics>): Double {
        return coinRiskMetrics.sumOf { it.contributionToRisk }
    }

    private fun calculateDiversificationScore(coinCount: Int): Double {
        val normalizedCount = coinCount.coerceAtMost(MAX_DIVERSIFICATION_COINS)
        return (normalizedCount.toDouble() / MAX_DIVERSIFICATION_COINS) * 100
    }

    private fun calculateConcentrationRisk(coinRiskMetrics: List<CoinRiskMetrics>): Double {
        if (coinRiskMetrics.isEmpty()) return 0.0
        val maxAllocation = coinRiskMetrics.maxOf { it.allocationPercentage }
        return maxAllocation
    }

    private fun calculateRiskScore(
        portfolioVolatility: Double,
        diversificationScore: Double,
        concentrationRisk: Double
    ): Double {
        val volatilityComponent = (portfolioVolatility.coerceIn(0.0, 50.0) / 50.0) * 100 * VOLATILITY_WEIGHT
        val diversificationComponent = (100 - diversificationScore) * DIVERSIFICATION_WEIGHT
        val concentrationComponent = concentrationRisk * CONCENTRATION_WEIGHT
        return (volatilityComponent + diversificationComponent + concentrationComponent).coerceIn(0.0, 100.0)
    }

    private fun determineRiskLevel(riskScore: Double): RiskLevel {
        return when {
            riskScore <= LOW_RISK_THRESHOLD -> RiskLevel.LOW
            riskScore <= MODERATE_RISK_THRESHOLD -> RiskLevel.MODERATE
            riskScore <= HIGH_RISK_THRESHOLD -> RiskLevel.HIGH
            else -> RiskLevel.CRITICAL
        }
    }

    private fun createEmptyRiskAnalysis(): PortfolioRiskAnalysis {
        return PortfolioRiskAnalysis(
            portfolioVolatility = 0.0,
            riskScore = 0.0,
            riskLevel = RiskLevel.LOW,
            diversificationScore = 0.0,
            concentrationRisk = 0.0,
            coinRiskMetrics = emptyList()
        )
    }
}
