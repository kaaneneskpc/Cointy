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
                val coinList = coinResponse.data.map { coinItem ->
                    UiCoinListItem(
                        id = coinItem.coin.id,
                        symbol = coinItem.coin.symbol,
                        name = coinItem.coin.name,
                        iconUrl = coinItem.coin.iconUrl,
                        formattedPrice = formatCoinPrice(coinItem.price),
                        formattedChange = formatCoinPricePercentage(coinItem.change),
                        isPositive = coinItem.change >= 0,
                        price = coinItem.price,
                        change = coinItem.change
                    )
                }
                _state.update {
                    it.copy(
                        coins = coinList,
                        filteredCoins = applyFiltersAndSort(coinList, it.searchQuery, it.filterOption, it.sortOption)
                    )
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        coins = emptyList(),
                        filteredCoins = emptyList(),
                        error = coinResponse.error.toUiText()
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredCoins = applyFiltersAndSort(
                    currentState.coins,
                    query,
                    currentState.filterOption,
                    currentState.sortOption
                )
            )
        }
    }

    fun onSearchActiveChanged(isActive: Boolean) {
        _state.update { it.copy(isSearchActive = isActive) }
        if (!isActive) {
            onSearchQueryChanged("")
        }
    }

    fun onSortOptionChanged(sortOption: CoinSortOption) {
        _state.update { currentState ->
            currentState.copy(
                sortOption = sortOption,
                filteredCoins = applyFiltersAndSort(
                    currentState.coins,
                    currentState.searchQuery,
                    currentState.filterOption,
                    sortOption
                )
            )
        }
    }

    fun onFilterOptionChanged(filterOption: CoinFilterOption) {
        _state.update { currentState ->
            currentState.copy(
                filterOption = filterOption,
                filteredCoins = applyFiltersAndSort(
                    currentState.coins,
                    currentState.searchQuery,
                    filterOption,
                    currentState.sortOption
                )
            )
        }
    }

    private fun applyFiltersAndSort(
        coins: List<UiCoinListItem>,
        searchQuery: String,
        filterOption: CoinFilterOption,
        sortOption: CoinSortOption
    ): List<UiCoinListItem> {
        return coins
            .filter { coin -> matchesSearchQuery(coin, searchQuery) }
            .filter { coin -> matchesFilterOption(coin, filterOption) }
            .let { filteredList -> applySortOption(filteredList, sortOption) }
    }

    private fun matchesSearchQuery(coin: UiCoinListItem, query: String): Boolean {
        if (query.isBlank()) return true
        val lowerQuery = query.lowercase()
        return coin.name.lowercase().contains(lowerQuery) ||
                coin.symbol.lowercase().contains(lowerQuery)
    }

    private fun matchesFilterOption(coin: UiCoinListItem, filterOption: CoinFilterOption): Boolean {
        return when (filterOption) {
            CoinFilterOption.ALL -> true
            CoinFilterOption.GAINERS -> coin.isPositive
            CoinFilterOption.LOSERS -> !coin.isPositive
        }
    }

    private fun applySortOption(coins: List<UiCoinListItem>, sortOption: CoinSortOption): List<UiCoinListItem> {
        return when (sortOption) {
            CoinSortOption.NAME_ASC -> coins.sortedBy { it.name.lowercase() }
            CoinSortOption.NAME_DESC -> coins.sortedByDescending { it.name.lowercase() }
            CoinSortOption.PRICE_ASC -> coins.sortedBy { it.price }
            CoinSortOption.PRICE_DESC -> coins.sortedByDescending { it.price }
            CoinSortOption.CHANGE_ASC -> coins.sortedBy { it.change }
            CoinSortOption.CHANGE_DESC -> coins.sortedByDescending { it.change }
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