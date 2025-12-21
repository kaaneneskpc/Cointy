package com.kaaneneskpc.cointy.export.domain.model

import com.kaaneneskpc.cointy.portfolio.domain.PortfolioCoinModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionModel

data class ExportData(
    val portfolioCoins: List<PortfolioCoinModel>,
    val transactions: List<TransactionModel>,
    val cashBalance: Double,
    val totalPortfolioValue: Double,
    val exportTimestamp: Long
)

data class PortfolioExportItem(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double,
    val performancePercent: Double
)

data class TransactionExportItem(
    val id: Long,
    val type: String,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val amountInFiat: Double,
    val amountInUnit: Double,
    val price: Double,
    val timestamp: Long,
    val formattedDate: String
)

