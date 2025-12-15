package com.kaaneneskpc.cointy.trade.presentation.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.coins.domain.GetCoinDetailsUseCase
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioRepository
import com.kaaneneskpc.cointy.trade.domain.SellCoinUseCase
import com.kaaneneskpc.cointy.trade.presentation.common.TradeState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.toUiText
import com.kaaneneskpc.cointy.trade.mapper.toCoin
import com.kaaneneskpc.cointy.trade.presentation.common.UiTradeCoinItem

class SellViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val sellCoinUseCase: SellCoinUseCase,
    private val coinId: String,
): ViewModel() {

    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())
    val state = combine(
        _state,
        _amount,
    ) { state, amount ->
        state.copy(
            amount = amount
        )
    }.onStart {
        when(val portfolioCoinResponse = portfolioRepository.getPortfolioCoin(coinId)) {
            is Result.Success -> {
                portfolioCoinResponse.data?.ownedAmountInUnit?.let {
                    getCoinDetails(it)
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = portfolioCoinResponse.error.toUiText()
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true)
    )

    private val _events = Channel<SellEvents>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAmountChanged(amount: String) {
        _amount.value = amount
    }

    private suspend fun getCoinDetails(ownedAmountInUnit: Double) {
        when(val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
            is Result.Success -> {
                val availableAmountInFiat = ownedAmountInUnit * coinResponse.data.price
                _state.update {
                    it.copy(
                        coin = UiTradeCoinItem(
                            id = coinResponse.data.coin.id,
                            name = coinResponse.data.coin.name,
                            symbol = coinResponse.data.coin.symbol,
                            iconUrl = coinResponse.data.coin.iconUrl,
                            price = coinResponse.data.price,
                        ),
                        availableAmount = "Available: ${formatCoinPrice(availableAmountInFiat)}"
                    )
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = coinResponse.error.toUiText()
                    )
                }
            }
        }
    }

    fun onSellClicked() {
        val tradeCoin = state.value.coin ?: return
        viewModelScope.launch {
            val sellCoinResponse = sellCoinUseCase.sellCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = _amount.value.toDouble(),
                price = tradeCoin.price
            )
            when (sellCoinResponse) {
                is Result.Success -> {
                    _events.send(SellEvents.SellSuccess)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = sellCoinResponse.error.toUiText()
                        )
                    }
                }
            }
        }
    }
}

sealed interface SellEvents {
    data object SellSuccess : SellEvents
}