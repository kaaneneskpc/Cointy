package com.kaaneneskpc.cointy.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPriceUnit
import com.kaaneneskpc.cointy.transaction.domain.GetTransactionHistoryUseCase
import com.kaaneneskpc.cointy.transaction.domain.TransactionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TransactionHistoryViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    val state: StateFlow<TransactionHistoryState> = getTransactionHistoryUseCase
        .getAllTransactions()
        .map { transactions ->
            TransactionHistoryState(
                isLoading = false,
                transactions = transactions.map { transaction ->
                    transaction.toUiTransactionItem()
                }
            )
        }
        .onStart {
            emit(TransactionHistoryState(isLoading = true))
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionHistoryState(isLoading = true)
        )
}

private fun com.kaaneneskpc.cointy.transaction.domain.TransactionModel.toUiTransactionItem(): UiTransactionItem {
    return UiTransactionItem(
        id = id,
        type = when (type) {
            TransactionType.BUY -> "BUY"
            TransactionType.SELL -> "SELL"
        },
        coinName = coin.name,
        coinSymbol = coin.symbol,
        coinIconUrl = coin.iconUrl,
        amountInFiat = formatCoinPrice(amountInFiat),
        amountInUnit = formatCoinPriceUnit(amountInUnit, coin.symbol),
        price = formatCoinPrice(price),
        formattedDate = formatTimestamp(timestamp),
        isBuy = type == TransactionType.BUY
    )
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.date} ${localDateTime.time.hour.toString().padStart(2, '0')}:${localDateTime.time.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        ""
    }
}

