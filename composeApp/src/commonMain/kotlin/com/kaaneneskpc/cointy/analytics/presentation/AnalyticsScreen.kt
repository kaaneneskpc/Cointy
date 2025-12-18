package com.kaaneneskpc.cointy.analytics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaaneneskpc.cointy.analytics.presentation.component.AnalyticsSummaryCard
import com.kaaneneskpc.cointy.analytics.presentation.component.CoinPerformanceItem
import com.kaaneneskpc.cointy.analytics.presentation.component.PieChartLegend
import com.kaaneneskpc.cointy.analytics.presentation.component.PortfolioHistoryChart
import com.kaaneneskpc.cointy.analytics.presentation.component.PortfolioPieChart
import com.kaaneneskpc.cointy.analytics.presentation.component.TransactionStatsCard
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.theme.LocalCoinRoutineColorsPalette
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBackClicked: () -> Unit
) {
    val viewModel = koinViewModel<AnalyticsViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Portfolio Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> {
                    LoadingContent()
                }
                state.error != null -> {
                    ErrorContent(errorMessage = state.error ?: "Unknown error")
                }
                !state.hasData -> {
                    EmptyAnalyticsContent()
                }
                else -> {
                    AnalyticsContent(state = state)
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = LocalCoinRoutineColorsPalette.current.profitGreen,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ErrorContent(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyAnalyticsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Analytics Data",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start investing to see your portfolio analytics and performance charts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AnalyticsContent(state: AnalyticsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfitLossSummarySection(state = state)
        }
        item {
            PortfolioDistributionSection(state = state)
        }
        if (state.portfolioHistory.size >= 2) {
            item {
                PortfolioHistorySection(state = state)
            }
        }
        item {
            TransactionStatsCard(
                totalTransactions = state.totalTransactionCount,
                buyCount = state.buyTransactionCount,
                sellCount = state.sellTransactionCount
            )
        }
        if (state.coinPerformances.isNotEmpty()) {
            item {
                Text(
                    text = "Coin Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(state.coinPerformances) { performance ->
                CoinPerformanceItem(
                    performance = performance,
                    formattedCurrentValue = formatCoinPrice(performance.currentValue),
                    formattedProfitLoss = formatProfitLossValue(performance.profitLoss),
                    formattedPercentage = formatCoinPricePercentage(performance.profitLossPercentage)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfitLossSummarySection(state: AnalyticsState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Total Value",
                value = state.totalPortfolioValue
            )
            AnalyticsSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Invested",
                value = state.totalInvestedAmount
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.isPositivePerformance) {
                    LocalCoinRoutineColorsPalette.current.profitGreen.copy(alpha = 0.1f)
                } else {
                    LocalCoinRoutineColorsPalette.current.lossRed.copy(alpha = 0.1f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Profit/Loss",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.totalProfitLoss,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isPositivePerformance) {
                            LocalCoinRoutineColorsPalette.current.profitGreen
                        } else {
                            LocalCoinRoutineColorsPalette.current.lossRed
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = if (state.isPositivePerformance) {
                                LocalCoinRoutineColorsPalette.current.profitGreen
                            } else {
                                LocalCoinRoutineColorsPalette.current.lossRed
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = state.profitLossPercentage,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioDistributionSection(state: AnalyticsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Portfolio Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PortfolioPieChart(
                    distributions = state.coinDistributions,
                    totalValue = state.totalPortfolioValue
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            PieChartLegend(distributions = state.coinDistributions)
        }
    }
}

@Composable
private fun PortfolioHistorySection(state: AnalyticsState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Portfolio History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                PortfolioHistoryChart(
                    historyPoints = state.portfolioHistory,
                    profitColor = LocalCoinRoutineColorsPalette.current.profitGreen,
                    lossColor = LocalCoinRoutineColorsPalette.current.lossRed
                )
            }
        }
    }
}

private fun formatProfitLossValue(amount: Double): String {
    val prefix = if (amount >= 0) "+" else ""
    return prefix + formatCoinPrice(amount)
}

