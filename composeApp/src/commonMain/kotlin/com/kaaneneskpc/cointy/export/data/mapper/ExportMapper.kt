package com.kaaneneskpc.cointy.export.data.mapper

import com.kaaneneskpc.cointy.export.domain.model.PortfolioExportItem
import com.kaaneneskpc.cointy.export.domain.model.TransactionExportItem
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioCoinModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun PortfolioCoinModel.toExportItem(): PortfolioExportItem {
    return PortfolioExportItem(
        coinId = coin.id,
        coinName = coin.name,
        coinSymbol = coin.symbol,
        averagePurchasePrice = averagePurchasePrice,
        ownedAmountInUnit = ownedAmountInUnit,
        ownedAmountInFiat = ownedAmountInFiat,
        performancePercent = performancePercent
    )
}

fun TransactionModel.toExportItem(): TransactionExportItem {
    return TransactionExportItem(
        id = id,
        type = type.name,
        coinId = coin.id,
        coinName = coin.name,
        coinSymbol = coin.symbol,
        amountInFiat = amountInFiat,
        amountInUnit = amountInUnit,
        price = price,
        timestamp = timestamp,
        formattedDate = formatTimestamp(timestamp)
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')} " +
            "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
}


