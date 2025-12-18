package com.kaaneneskpc.cointy.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.alert.domain.CheckPriceAlertsUseCase
import com.kaaneneskpc.cointy.coins.domain.GetCoinPriceHistoryUseCase
import com.kaaneneskpc.cointy.coins.domain.GetCoinsListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.core.util.toUiText
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase,
    private val checkPriceAlertsUseCase: CheckPriceAlertsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CoinState())
    val state = _state
        .onStart {
            getAllCoins()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinState()
        )

    private suspend fun getAllCoins() {
        when (val coinResponse = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                val coinPrices = coinResponse.data.associate { it.coin.id to it.price }
                checkPriceAlerts(coinPrices)
                _state.update {
                    CoinState(
                        coins = coinResponse.data.map { coinItem ->
                            UiCoinListItem(
                                id = coinItem.coin.id,
                                symbol = coinItem.coin.symbol,
                                name = coinItem.coin.name,
                                iconUrl = coinItem.coin.iconUrl,
                                formattedPrice = formatCoinPrice(coinItem.price),
                                formattedChange = formatCoinPricePercentage(coinItem.change),
                                isPositive = coinItem.change >= 0
                            )
                        }
                    )
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        coins = emptyList(),
                        error = coinResponse.error.toUiText()
                    )
                }
            }
        }
    }
    private suspend fun checkPriceAlerts(coinPrices: Map<String, Double>) {
        try {
            checkPriceAlertsUseCase.execute(coinPrices)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onCoinLongPressed(coinId: String) {
        _state.update {
            it.copy(
                chartState = UiChartState(
                    sparkLine = emptyList(),
                    isLoading = true,
                )
            )
        }

        viewModelScope.launch {
            when (val priceHistory = getCoinPriceHistoryUseCase.execute(coinId)) {
                is Result.Success -> {
                    _state.update { state ->
                        state.copy(
                            chartState = UiChartState(
                                sparkLine = priceHistory.data.sortedBy { it.timestamp }
                                    .map { it.price },
                                isLoading = false,
                                coinName = _state.value.coins.find { it.id == coinId }?.name.orEmpty()
                            )
                        )
                    }
                }

                is Result.Error -> {
                    _state.update { state ->
                        state.copy(
                            chartState = UiChartState(
                                sparkLine = emptyList(),
                                isLoading = false,
                                coinName = ""
                            )
                        )
                    }
                }
            }
        }
    }

    fun onDismissChart() {
        _state.update {
            it.copy(chartState = null)
        }
    }
}