package com.kaaneneskpc.cointy.risk.presentation

import com.kaaneneskpc.cointy.risk.domain.model.CoinRiskMetrics
import com.kaaneneskpc.cointy.risk.domain.model.RiskLevel

data class RiskAnalysisState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val riskScore: String = "0",
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val portfolioVolatility: String = "0.00%",
    val diversificationScore: String = "0%",
    val concentrationRisk: String = "0%",
    val coinRiskMetrics: List<CoinRiskMetrics> = emptyList(),
    val hasData: Boolean = false
)
