package com.kaaneneskpc.cointy.export.data

import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.export.FileExporter
import com.kaaneneskpc.cointy.export.data.mapper.toExportItem
import com.kaaneneskpc.cointy.export.domain.ExportRepository
import com.kaaneneskpc.cointy.export.domain.model.ExportData
import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioRepository
import com.kaaneneskpc.cointy.transaction.domain.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant.Companion.fromEpochMilliseconds
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExportRepositoryImpl(
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository,
    private val fileExporter: FileExporter
) : ExportRepository {
    private val json = Json { prettyPrint = true }
    override fun getExportData(): Flow<ExportData> {
        return combine(
            portfolioRepository.allPortfolioCoinsFlow().map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyList()
                }
            },
            transactionRepository.getAllTransactions(),
            portfolioRepository.cashBalanceFlow(),
            portfolioRepository.calculateTotalPortfolioValue().map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> 0.0
                }
            }
        ) { portfolioCoins, transactions, cashBalance, totalValue ->
            ExportData(
                portfolioCoins = portfolioCoins,
                transactions = transactions,
                cashBalance = cashBalance,
                totalPortfolioValue = totalValue,
                exportTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }
    override suspend fun exportToFormat(data: ExportData, format: ExportFormat): ExportResult {
        val content = when (format) {
            ExportFormat.CSV -> generateCsvContent(data)
            ExportFormat.JSON -> generateJsonContent(data)
        }
        val fileName = fileExporter.generateFileName("cointy_portfolio", format)
        return fileExporter.exportFile(content, fileName, format)
    }
    override fun generateCsvContent(data: ExportData): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("COINTY PORTFOLIO EXPORT")
        stringBuilder.appendLine("Export Date,${formatTimestamp(data.exportTimestamp)}")
        stringBuilder.appendLine("Total Portfolio Value,${data.totalPortfolioValue}")
        stringBuilder.appendLine("Cash Balance,${data.cashBalance}")
        stringBuilder.appendLine()
        stringBuilder.appendLine("PORTFOLIO HOLDINGS")
        stringBuilder.appendLine("Coin ID,Coin Name,Symbol,Amount (Units),Amount (Fiat),Avg Purchase Price,Performance %")
        data.portfolioCoins.forEach { coin ->
            val item = coin.toExportItem()
            stringBuilder.appendLine("${item.coinId},${item.coinName},${item.coinSymbol},${item.ownedAmountInUnit},${item.ownedAmountInFiat},${item.averagePurchasePrice},${item.performancePercent}")
        }
        stringBuilder.appendLine()
        stringBuilder.appendLine("TRANSACTION HISTORY")
        stringBuilder.appendLine("ID,Type,Coin ID,Coin Name,Symbol,Amount (Fiat),Amount (Units),Price,Date")
        data.transactions.forEach { transaction ->
            val item = transaction.toExportItem()
            stringBuilder.appendLine("${item.id},${item.type},${item.coinId},${item.coinName},${item.coinSymbol},${item.amountInFiat},${item.amountInUnit},${item.price},${item.formattedDate}")
        }
        return stringBuilder.toString()
    }
    override fun generateJsonContent(data: ExportData): String {
        val exportJson = ExportJsonData(
            exportDate = formatTimestamp(data.exportTimestamp),
            summary = ExportSummary(
                totalPortfolioValue = data.totalPortfolioValue,
                cashBalance = data.cashBalance,
                totalCoins = data.portfolioCoins.size,
                totalTransactions = data.transactions.size
            ),
            portfolioHoldings = data.portfolioCoins.map { it.toExportItem() }.map { item ->
                PortfolioHoldingJson(
                    coinId = item.coinId,
                    coinName = item.coinName,
                    symbol = item.coinSymbol,
                    amountInUnits = item.ownedAmountInUnit,
                    amountInFiat = item.ownedAmountInFiat,
                    averagePurchasePrice = item.averagePurchasePrice,
                    performancePercent = item.performancePercent
                )
            },
            transactions = data.transactions.map { it.toExportItem() }.map { item ->
                TransactionJson(
                    id = item.id,
                    type = item.type,
                    coinId = item.coinId,
                    coinName = item.coinName,
                    symbol = item.coinSymbol,
                    amountInFiat = item.amountInFiat,
                    amountInUnits = item.amountInUnit,
                    price = item.price,
                    date = item.formattedDate
                )
            }
        )
        return json.encodeToString(exportJson)
    }
    private fun formatTimestamp(timestamp: Long): String {
        val instant = fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(currentSystemDefault())
        return "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')} " +
                "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }
}

@Serializable
private data class ExportJsonData(
    val exportDate: String,
    val summary: ExportSummary,
    val portfolioHoldings: List<PortfolioHoldingJson>,
    val transactions: List<TransactionJson>
)

@Serializable
private data class ExportSummary(
    val totalPortfolioValue: Double,
    val cashBalance: Double,
    val totalCoins: Int,
    val totalTransactions: Int
)

@Serializable
private data class PortfolioHoldingJson(
    val coinId: String,
    val coinName: String,
    val symbol: String,
    val amountInUnits: Double,
    val amountInFiat: Double,
    val averagePurchasePrice: Double,
    val performancePercent: Double
)

@Serializable
private data class TransactionJson(
    val id: Long,
    val type: String,
    val coinId: String,
    val coinName: String,
    val symbol: String,
    val amountInFiat: Double,
    val amountInUnits: Double,
    val price: Double,
    val date: String
)

