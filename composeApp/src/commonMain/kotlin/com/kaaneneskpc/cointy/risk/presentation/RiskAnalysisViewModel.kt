package com.kaaneneskpc.cointy.risk.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.core.util.toUiText
import com.kaaneneskpc.cointy.risk.domain.GetPortfolioRiskAnalysisUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class RiskAnalysisViewModel(
    private val getPortfolioRiskAnalysisUseCase: GetPortfolioRiskAnalysisUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _state = MutableStateFlow(RiskAnalysisState(isLoading = true))
    val state: StateFlow<RiskAnalysisState> = _state.asStateFlow()

    init {
        loadRiskAnalysis()
    }

    private fun loadRiskAnalysis() {
        getPortfolioRiskAnalysisUseCase()
            .onStart {
                _state.value = _state.value.copy(isLoading = true)
            }
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        val analysis = result.data
                        _state.value = RiskAnalysisState(
                            isLoading = false,
                            error = null,
                            riskScore = formatRiskScore(analysis.riskScore),
                            riskLevel = analysis.riskLevel,
                            portfolioVolatility = formatCoinPricePercentage(analysis.portfolioVolatility),
                            diversificationScore = formatCoinPricePercentage(analysis.diversificationScore),
                            concentrationRisk = formatCoinPricePercentage(analysis.concentrationRisk),
                            coinRiskMetrics = analysis.coinRiskMetrics,
                            hasData = analysis.coinRiskMetrics.isNotEmpty()
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

    private fun formatRiskScore(score: Double): String {
        return score.toInt().toString()
    }
}
