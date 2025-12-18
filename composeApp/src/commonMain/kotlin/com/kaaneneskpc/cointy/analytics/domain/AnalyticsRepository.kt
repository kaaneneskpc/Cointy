package com.kaaneneskpc.cointy.analytics.domain

import com.kaaneneskpc.cointy.analytics.domain.model.CoinDistribution
import com.kaaneneskpc.cointy.analytics.domain.model.CoinPerformance
import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioAnalytics
import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioHistoryPoint
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getPortfolioAnalytics(): Flow<Result<PortfolioAnalytics, DataError.Remote>>
    fun getCoinDistributions(): Flow<Result<List<CoinDistribution>, DataError.Remote>>
    fun getCoinPerformances(): Flow<Result<List<CoinPerformance>, DataError.Remote>>
    fun getPortfolioHistory(): Flow<Result<List<PortfolioHistoryPoint>, DataError.Remote>>
    suspend fun calculateTotalInvestedAmount(): Double
    suspend fun calculateTotalProfitLoss(): Result<Double, DataError.Remote>
}

