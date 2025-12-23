package com.kaaneneskpc.cointy.risk.domain.model

data class PortfolioRiskAnalysis(
    val portfolioVolatility: Double,
    val riskScore: Double,
    val riskLevel: RiskLevel,
    val diversificationScore: Double,
    val concentrationRisk: Double,
    val coinRiskMetrics: List<CoinRiskMetrics>
)
