package com.kaaneneskpc.cointy.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

enum class CoinSortOption {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    CHANGE_ASC,
    CHANGE_DESC
}

enum class CoinFilterOption {
    ALL,
    GAINERS,
    LOSERS
}

@Stable
data class CoinState(
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
    val filteredCoins: List<UiCoinListItem> = emptyList(),
    val chartState: UiChartState? = null,
    val searchQuery: String = "",
    val sortOption: CoinSortOption = CoinSortOption.NAME_ASC,
    val filterOption: CoinFilterOption = CoinFilterOption.ALL,
    val isSearchActive: Boolean = false
)

@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val timestamps: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
    val coinSymbol: String = "",
)