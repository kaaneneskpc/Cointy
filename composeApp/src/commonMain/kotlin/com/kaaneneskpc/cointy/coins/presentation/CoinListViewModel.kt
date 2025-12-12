package com.kaaneneskpc.cointy.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class CoinListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase
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
}