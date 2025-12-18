package com.kaaneneskpc.cointy.analytics.domain.model

data class PortfolioAnalytics(
    val totalPortfolioValue: Double,
    val totalInvestedAmount: Double,
    val totalProfitLoss: Double,
    val profitLossPercentage: Double,
    val coinDistributions: List<CoinDistribution>,
    val portfolioHistory: List<PortfolioHistoryPoint>,
    val coinPerformances: List<CoinPerformance>,
    val totalTransactionCount: Int,
    val buyTransactionCount: Int,
    val sellTransactionCount: Int
)

data class CoinDistribution(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val valueInFiat: Double,
    val percentage: Double,
    val color: Long
)

data class PortfolioHistoryPoint(
    val timestamp: Long,
    val totalValue: Double
)

data class CoinPerformance(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val currentValue: Double,
    val investedAmount: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double,
    val isPositive: Boolean
)

