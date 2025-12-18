package com.kaaneneskpc.cointy.analytics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.analytics.domain.GetPortfolioAnalyticsUseCase
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.core.util.toUiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class AnalyticsViewModel(
    private val getPortfolioAnalyticsUseCase: GetPortfolioAnalyticsUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState(isLoading = true))
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        getPortfolioAnalyticsUseCase()
            .onStart {
                _state.value = _state.value.copy(isLoading = true)
            }
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        val analytics = result.data
                        _state.value = AnalyticsState(
                            isLoading = false,
                            error = null,
                            totalPortfolioValue = formatCoinPrice(analytics.totalPortfolioValue),
                            totalInvestedAmount = formatCoinPrice(analytics.totalInvestedAmount),
                            totalProfitLoss = formatProfitLoss(analytics.totalProfitLoss),
                            profitLossPercentage = formatCoinPricePercentage(analytics.profitLossPercentage),
                            isPositivePerformance = analytics.totalProfitLoss >= 0,
                            coinDistributions = analytics.coinDistributions,
                            portfolioHistory = analytics.portfolioHistory,
                            coinPerformances = analytics.coinPerformances,
                            totalTransactionCount = analytics.totalTransactionCount,
                            buyTransactionCount = analytics.buyTransactionCount,
                            sellTransactionCount = analytics.sellTransactionCount,
                            hasData = analytics.coinDistributions.isNotEmpty()
                        )
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.error.toUiText().toString()
                        )
                    }
                }
            }
            .flowOn(coroutineDispatcher)
            .launchIn(viewModelScope)
    }

    private fun formatProfitLoss(amount: Double): String {
        val prefix = if (amount >= 0) "+" else ""
        return prefix + formatCoinPrice(amount)
    }
}

