package com.kaaneneskpc.cointy.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPriceUnit
import com.kaaneneskpc.cointy.transaction.domain.GetTransactionHistoryUseCase
import com.kaaneneskpc.cointy.transaction.domain.TransactionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TransactionHistoryViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _filterOption = MutableStateFlow(TransactionFilterOption.ALL)
    private val _sortOption = MutableStateFlow(TransactionSortOption.DATE_DESC)
    private val _isSearchActive = MutableStateFlow(false)

    private val transactionsFlow = getTransactionHistoryUseCase
        .getAllTransactions()
        .map { transactions ->
            transactions.map { transaction -> transaction.toUiTransactionItem() }
        }

    val state: StateFlow<TransactionHistoryState> = combine(
        transactionsFlow,
        _searchQuery,
        _filterOption,
        _sortOption,
        _isSearchActive
    ) { transactions, searchQuery, filterOption, sortOption, isSearchActive ->
        val filteredTransactions = applyFiltersAndSort(
            transactions,
            searchQuery,
            filterOption,
            sortOption
        )
        TransactionHistoryState(
            isLoading = false,
            transactions = transactions,
            filteredTransactions = filteredTransactions,
            searchQuery = searchQuery,
            filterOption = filterOption,
            sortOption = sortOption,
            isSearchActive = isSearchActive
        )
    }
        .onStart { emit(TransactionHistoryState(isLoading = true)) }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionHistoryState(isLoading = true)
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.update { query }
    }

    fun onSearchActiveChanged(isActive: Boolean) {
        _isSearchActive.update { isActive }
        if (!isActive) {
            _searchQuery.update { "" }
        }
    }

    fun onFilterOptionChanged(filterOption: TransactionFilterOption) {
        _filterOption.update { filterOption }
    }

    fun onSortOptionChanged(sortOption: TransactionSortOption) {
        _sortOption.update { sortOption }
    }

    private fun applyFiltersAndSort(
        transactions: List<UiTransactionItem>,
        searchQuery: String,
        filterOption: TransactionFilterOption,
        sortOption: TransactionSortOption
    ): List<UiTransactionItem> {
        return transactions
            .filter { transaction -> matchesSearchQuery(transaction, searchQuery) }
            .filter { transaction -> matchesFilterOption(transaction, filterOption) }
            .let { filteredList -> applySortOption(filteredList, sortOption) }
    }

    private fun matchesSearchQuery(transaction: UiTransactionItem, query: String): Boolean {
        if (query.isBlank()) return true
        val lowerQuery = query.lowercase()
        return transaction.coinName.lowercase().contains(lowerQuery) ||
                transaction.coinSymbol.lowercase().contains(lowerQuery)
    }

    private fun matchesFilterOption(transaction: UiTransactionItem, filterOption: TransactionFilterOption): Boolean {
        return when (filterOption) {
            TransactionFilterOption.ALL -> true
            TransactionFilterOption.BUY_ONLY -> transaction.isBuy
            TransactionFilterOption.SELL_ONLY -> !transaction.isBuy
        }
    }

    private fun applySortOption(transactions: List<UiTransactionItem>, sortOption: TransactionSortOption): List<UiTransactionItem> {
        return when (sortOption) {
            TransactionSortOption.DATE_DESC -> transactions.sortedByDescending { it.timestamp }
            TransactionSortOption.DATE_ASC -> transactions.sortedBy { it.timestamp }
            TransactionSortOption.AMOUNT_DESC -> transactions.sortedByDescending { it.amountInFiatValue }
            TransactionSortOption.AMOUNT_ASC -> transactions.sortedBy { it.amountInFiatValue }
        }
    }
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
        isBuy = type == TransactionType.BUY,
        amountInFiatValue = amountInFiat,
        timestamp = timestamp
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

