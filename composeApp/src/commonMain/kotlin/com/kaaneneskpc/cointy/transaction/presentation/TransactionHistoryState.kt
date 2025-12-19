package com.kaaneneskpc.cointy.transaction.presentation

enum class TransactionFilterOption {
    ALL,
    BUY_ONLY,
    SELL_ONLY
}

enum class TransactionSortOption {
    DATE_DESC,
    DATE_ASC,
    AMOUNT_DESC,
    AMOUNT_ASC
}

data class TransactionHistoryState(
    val isLoading: Boolean = true,
    val transactions: List<UiTransactionItem> = emptyList(),
    val filteredTransactions: List<UiTransactionItem> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val filterOption: TransactionFilterOption = TransactionFilterOption.ALL,
    val sortOption: TransactionSortOption = TransactionSortOption.DATE_DESC,
    val isSearchActive: Boolean = false
)

data class UiTransactionItem(
    val id: Long,
    val type: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val amountInFiat: String,
    val amountInUnit: String,
    val price: String,
    val formattedDate: String,
    val isBuy: Boolean,
    val amountInFiatValue: Double = 0.0,
    val timestamp: Long = 0L
)


