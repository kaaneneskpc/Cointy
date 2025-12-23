package com.kaaneneskpc.cointy.risk.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.risk.domain.model.PortfolioRiskAnalysis
import kotlinx.coroutines.flow.Flow

class GetPortfolioRiskAnalysisUseCase(
    private val riskAnalysisRepository: RiskAnalysisRepository
) {
    operator fun invoke(): Flow<Result<PortfolioRiskAnalysis, DataError.Remote>> {
        return riskAnalysisRepository.getPortfolioRiskAnalysis()
    }
}
