package com.kaaneneskpc.cointy.analytics.domain

import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioAnalytics
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import kotlinx.coroutines.flow.Flow

class GetPortfolioAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(): Flow<Result<PortfolioAnalytics, DataError.Remote>> {
        return analyticsRepository.getPortfolioAnalytics()
    }
}

