package com.kaaneneskpc.cointy.transaction.presentation

data class TransactionHistoryState(
    val isLoading: Boolean = true,
    val transactions: List<UiTransactionItem> = emptyList(),
    val error: String? = null
)

data class UiTransactionItem(
    val id: Long,
    val type: String, // "BUY" or "SELL"
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val amountInFiat: String,
    val amountInUnit: String,
    val price: String,
    val formattedDate: String,
    val isBuy: Boolean
)

