package com.kaaneneskpc.cointy.coins.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kaaneneskpc.cointy.coins.presentation.component.CoinChart
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.theme.LocalCoinRoutineColorsPalette
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun CoinListScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinListViewModel = koinViewModel<CoinListViewModel>()
    val state by coinListViewModel.state.collectAsStateWithLifecycle()

    CoinListContent(
        state = state,
        onDismissChart = { coinListViewModel.onDismissChart() },
        onCoinLongPressed = { coinId -> coinListViewModel.onCoinLongPressed(coinId) },
        onCoinClicked = onCoinClicked
    )

}

@Composable
fun CoinListContent(
    state: CoinState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        if (state.chartState != null) {
            CoinChartDialog(
                uiChartState = state.chartState,
                onDismiss = onDismissChart,
            )
        }
        
        when {
            state.coins.isEmpty() && state.error == null -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LocalCoinRoutineColorsPalette.current.profitGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            state.error != null -> {
                // Error state
                ErrorContent(error = state.error)
            }
            else -> {
                CoinList(
                    coins = state.coins,
                    onCoinLongPressed = onCoinLongPressed,
                    onCoinClicked = onCoinClicked
                )
            }
        }
    }
}

@Composable
fun CoinList(
    coins: List<UiCoinListItem>,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 8.dp,
            bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 8.dp,
            start = 20.dp,
            end = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Modern Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔥",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = "Top Coins",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${coins.size} cryptocurrencies",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        
        items(coins) { coin ->
            CoinListItem(
                coin = coin,
                onCoinLongPressed = onCoinLongPressed,
                onCoinClicked = onCoinClicked
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoinListItem(
    coin: UiCoinListItem,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = { onCoinLongPressed(coin.id) },
                onClick = { onCoinClicked(coin.id) }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Coin Icon with Background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = coin.iconUrl,
                    contentDescription = "${coin.name} icon",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Coin Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = coin.symbol.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Price and Change
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = coin.formattedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (coin.isPositive) {
                                LocalCoinRoutineColorsPalette.current.profitGreen.copy(alpha = 0.15f)
                            } else {
                                LocalCoinRoutineColorsPalette.current.lossRed.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = coin.formattedChange,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (coin.isPositive) {
                            LocalCoinRoutineColorsPalette.current.profitGreen
                        } else {
                            LocalCoinRoutineColorsPalette.current.lossRed
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(error: org.jetbrains.compose.resources.StringResource) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(error),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please try again later",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun CoinChartDialog(
    uiChartState: UiChartState,
    onDismiss: () -> Unit,
) {
    val colors = LocalCoinRoutineColorsPalette.current
    val isPositive = uiChartState.sparkLine.isNotEmpty() && 
                     uiChartState.sparkLine.last() > uiChartState.sparkLine.first()
    
    val currentPrice = uiChartState.sparkLine.lastOrNull() ?: 0.0
    val minPrice = uiChartState.sparkLine.minOrNull() ?: 0.0
    val maxPrice = uiChartState.sparkLine.maxOrNull() ?: 0.0
    val priceChange = if (uiChartState.sparkLine.isNotEmpty()) {
        currentPrice - uiChartState.sparkLine.first()
    } else 0.0
    val priceChangePercent = if (uiChartState.sparkLine.isNotEmpty() && uiChartState.sparkLine.first() != 0.0) {
        (priceChange / uiChartState.sparkLine.first()) * 100
    } else 0.0
    
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiChartState.coinName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "24h Price Chart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        text = {
            if (uiChartState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = colors.profitGreen
                        )
                        Text(
                            text = "Loading chart data...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else if (uiChartState.sparkLine.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "No chart data available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Price Statistics Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Current Price Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Current",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$${formatCoinPrice(currentPrice)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        // Change Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPositive) {
                                    colors.profitGreen.copy(alpha = 0.15f)
                                } else {
                                    colors.lossRed.copy(alpha = 0.15f)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "24h Change",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatCoinPricePercentage(priceChangePercent),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) colors.profitGreen else colors.lossRed
                                )
                            }
                        }
                    }
                    
                    // Min/Max Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Low:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = formatCoinPrice(minPrice),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.lossRed
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "High:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = formatCoinPrice(maxPrice),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = colors.profitGreen
                            )
                        }
                    }
                    
                    // Chart Container with modern styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            CoinChart(
                                modifier = Modifier.fillMaxSize(),
                                nodes = uiChartState.sparkLine,
                                profitColor = colors.profitGreen,
                                lossColor = colors.lossRed,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
