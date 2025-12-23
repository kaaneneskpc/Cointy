package com.kaaneneskpc.cointy.risk.domain.model

data class CoinRiskMetrics(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val volatility: Double,
    val allocationPercentage: Double,
    val contributionToRisk: Double
)
