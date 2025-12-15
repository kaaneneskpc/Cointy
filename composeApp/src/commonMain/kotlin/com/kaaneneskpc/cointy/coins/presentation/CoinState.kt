package com.kaaneneskpc.cointy.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinState(
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
    val chartState: UiChartState? = null
)

@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
)