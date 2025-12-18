package com.kaaneneskpc.cointy.analytics.presentation

import com.kaaneneskpc.cointy.analytics.domain.model.CoinDistribution
import com.kaaneneskpc.cointy.analytics.domain.model.CoinPerformance
import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioHistoryPoint

data class AnalyticsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalPortfolioValue: String = "$0.00",
    val totalInvestedAmount: String = "$0.00",
    val totalProfitLoss: String = "$0.00",
    val profitLossPercentage: String = "0.00%",
    val isPositivePerformance: Boolean = true,
    val coinDistributions: List<CoinDistribution> = emptyList(),
    val portfolioHistory: List<PortfolioHistoryPoint> = emptyList(),
    val coinPerformances: List<CoinPerformance> = emptyList(),
    val totalTransactionCount: Int = 0,
    val buyTransactionCount: Int = 0,
    val sellTransactionCount: Int = 0,
    val hasData: Boolean = false
)

