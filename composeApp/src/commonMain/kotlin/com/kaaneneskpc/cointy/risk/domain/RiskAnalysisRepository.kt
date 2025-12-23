package com.kaaneneskpc.cointy.risk.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.risk.domain.model.PortfolioRiskAnalysis
import kotlinx.coroutines.flow.Flow

interface RiskAnalysisRepository {
    fun getPortfolioRiskAnalysis(): Flow<Result<PortfolioRiskAnalysis, DataError.Remote>>
}
