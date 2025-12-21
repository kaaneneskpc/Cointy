package com.kaaneneskpc.cointy.coins.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kaaneneskpc.cointy.coins.presentation.component.ChartDataPoint
import com.kaaneneskpc.cointy.coins.presentation.component.TradingViewChart
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import kotlinx.datetime.Clock
import com.kaaneneskpc.cointy.core.util.formatCoinPricePercentage
import com.kaaneneskpc.cointy.theme.LocalCoinRoutineColorsPalette
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CoinListScreen(
    onCoinClicked: (String) -> Unit,
    onCreateAlertClicked: (String) -> Unit = {}
) {
    val coinListViewModel = koinViewModel<CoinListViewModel>()
    val state by coinListViewModel.state.collectAsStateWithLifecycle()
    CoinListContent(
        state = state,
        onDismissChart = { coinListViewModel.onDismissChart() },
        onCoinLongPressed = { coinId -> coinListViewModel.onCoinLongPressed(coinId) },
        onCoinClicked = onCoinClicked,
        onCreateAlertClicked = onCreateAlertClicked,
        onSearchQueryChanged = { coinListViewModel.onSearchQueryChanged(it) },
        onSearchActiveChanged = { coinListViewModel.onSearchActiveChanged(it) },
        onSortOptionChanged = { coinListViewModel.onSortOptionChanged(it) },
        onFilterOptionChanged = { coinListViewModel.onFilterOptionChanged(it) }
    )
}

@Composable
fun CoinListContent(
    state: CoinState,
    onDismissChart: () -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
    onCreateAlertClicked: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit,
    onSortOptionChanged: (CoinSortOption) -> Unit,
    onFilterOptionChanged: (CoinFilterOption) -> Unit
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
                ErrorContent(error = state.error)
            }
            else -> {
                CoinListWithSearch(
                    state = state,
                    onCoinLongPressed = onCoinLongPressed,
                    onCoinClicked = onCoinClicked,
                    onCreateAlertClicked = onCreateAlertClicked,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onSearchActiveChanged = onSearchActiveChanged,
                    onSortOptionChanged = onSortOptionChanged,
                    onFilterOptionChanged = onFilterOptionChanged
                )
            }
        }
    }
}

@Composable
private fun CoinListWithSearch(
    state: CoinState,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
    onCreateAlertClicked: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit,
    onSortOptionChanged: (CoinSortOption) -> Unit,
    onFilterOptionChanged: (CoinFilterOption) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CoinSearchBar(
            searchQuery = state.searchQuery,
            isSearchActive = state.isSearchActive,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchActiveChanged = onSearchActiveChanged
        )
        CoinFilterSection(
            selectedFilter = state.filterOption,
            selectedSort = state.sortOption,
            onFilterOptionChanged = onFilterOptionChanged,
            onSortOptionChanged = onSortOptionChanged
        )
        CoinList(
            coins = state.filteredCoins,
            onCoinLongPressed = onCoinLongPressed,
            onCoinClicked = onCoinClicked,
            onCreateAlertClicked = onCreateAlertClicked
        )
    }
}

@Composable
private fun CoinSearchBar(
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 16.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 8.dp
            )
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChanged(it)
                if (!isSearchActive && it.isNotEmpty()) {
                    onSearchActiveChanged(true)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search coins...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = {
                        onSearchQueryChanged("")
                        onSearchActiveChanged(false)
                        keyboardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
        )
    }
}

@Composable
private fun CoinFilterSection(
    selectedFilter: CoinFilterOption,
    selectedSort: CoinSortOption,
    onFilterOptionChanged: (CoinFilterOption) -> Unit,
    onSortOptionChanged: (CoinSortOption) -> Unit
) {
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CoinFilterOption.entries.forEach { filterOption ->
                FilterChip(
                    selected = selectedFilter == filterOption,
                    onClick = { onFilterOptionChanged(filterOption) },
                    label = {
                        Text(
                            text = getFilterOptionLabel(filterOption),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selectedFilter == filterOption) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LocalCoinRoutineColorsPalette.current.profitGreen.copy(alpha = 0.2f),
                        selectedLabelColor = LocalCoinRoutineColorsPalette.current.profitGreen
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                FilterChip(
                    selected = true,
                    onClick = { isSortMenuExpanded = true },
                    label = {
                        Text(
                            text = "Sort: ${getSortOptionLabel(selectedSort)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                DropdownMenu(
                    expanded = isSortMenuExpanded,
                    onDismissRequest = { isSortMenuExpanded = false }
                ) {
                    CoinSortOption.entries.forEach { sortOption ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = getSortOptionLabel(sortOption),
                                    fontWeight = if (selectedSort == sortOption) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortOptionChanged(sortOption)
                                isSortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getFilterOptionLabel(option: CoinFilterOption): String {
    return when (option) {
        CoinFilterOption.ALL -> "All"
        CoinFilterOption.GAINERS -> "Gainers 📈"
        CoinFilterOption.LOSERS -> "Losers 📉"
    }
}

private fun getSortOptionLabel(option: CoinSortOption): String {
    return when (option) {
        CoinSortOption.NAME_ASC -> "Name A-Z"
        CoinSortOption.NAME_DESC -> "Name Z-A"
        CoinSortOption.PRICE_ASC -> "Price ↑"
        CoinSortOption.PRICE_DESC -> "Price ↓"
        CoinSortOption.CHANGE_ASC -> "Change ↑"
        CoinSortOption.CHANGE_DESC -> "Change ↓"
    }
}

@Composable
fun CoinList(
    coins: List<UiCoinListItem>,
    onCoinLongPressed: (String) -> Unit,
    onCoinClicked: (String) -> Unit,
    onCreateAlertClicked: (String) -> Unit
) {
    if (coins.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "🔍",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "No coins found",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try adjusting your search or filters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 8.dp,
            start = 20.dp,
            end = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Top Coins",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${coins.size} coins",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        items(coins, key = { it.id }) { coin ->
            CoinListItem(
                coin = coin,
                onCoinLongPressed = onCoinLongPressed,
                onCoinClicked = onCoinClicked,
                onCreateAlertClicked = onCreateAlertClicked
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
    onCreateAlertClicked: (String) -> Unit
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
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onCreateAlertClicked(coin.id) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Set Alert",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
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
private fun ErrorContent(error: StringResource) {
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
    val openPrice = uiChartState.sparkLine.firstOrNull() ?: 0.0
    val minPrice = uiChartState.sparkLine.minOrNull() ?: 0.0
    val maxPrice = uiChartState.sparkLine.maxOrNull() ?: 0.0
    val priceChange = if (uiChartState.sparkLine.isNotEmpty()) {
        currentPrice - uiChartState.sparkLine.first()
    } else 0.0
    val priceChangePercent = if (uiChartState.sparkLine.isNotEmpty() && uiChartState.sparkLine.first() != 0.0) {
        (priceChange / uiChartState.sparkLine.first()) * 100
    } else 0.0
    val chartDataPoints = remember(uiChartState.sparkLine, uiChartState.timestamps) {
        if (uiChartState.timestamps.size == uiChartState.sparkLine.size) {
            uiChartState.sparkLine.zip(uiChartState.timestamps).map { (price, timestamp) ->
                ChartDataPoint(price = price, timestamp = timestamp)
            }
        } else {
            val currentTimestamp = Clock.System.now().epochSeconds
            uiChartState.sparkLine.mapIndexed { index, price ->
                ChartDataPoint(price = price, timestamp = currentTimestamp - (uiChartState.sparkLine.size - index) * 3600)
            }
        }
    }
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color(0xFF1E222D),
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiChartState.coinName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (uiChartState.coinSymbol.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF2A2E39))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = uiChartState.coinSymbol.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF787B86)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2A2E39))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "24H",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2962FF)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formatCoinPrice(currentPrice),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isPositive) colors.profitGreen.copy(alpha = 0.2f)
                                else colors.lossRed.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = formatCoinPricePercentage(priceChangePercent),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) colors.profitGreen else colors.lossRed
                        )
                    }
                }
            }
        },
        text = {
            if (uiChartState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .background(Color(0xFF131722)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF2962FF)
                        )
                        Text(
                            text = "Loading chart data...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF787B86)
                        )
                    }
                }
            } else if (uiChartState.sparkLine.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .background(Color(0xFF131722)),
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
                            color = Color(0xFF787B86)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TradingViewPriceInfo(label = "O", value = openPrice, color = Color(0xFF787B86))
                        TradingViewPriceInfo(label = "H", value = maxPrice, color = colors.profitGreen)
                        TradingViewPriceInfo(label = "L", value = minPrice, color = colors.lossRed)
                        TradingViewPriceInfo(label = "C", value = currentPrice, color = if (isPositive) colors.profitGreen else colors.lossRed)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        TradingViewChart(
                            modifier = Modifier.fillMaxSize(),
                            dataPoints = chartDataPoints,
                            profitColor = colors.profitGreen,
                            lossColor = colors.lossRed,
                            backgroundColor = Color(0xFF131722),
                            gridColor = Color(0xFF2A2E39),
                            textColor = Color(0xFF787B86),
                            crosshairColor = Color(0xFF9598A1)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TradingViewStatItem(
                            label = "24h Volume",
                            value = formatVolumeDisplay(maxPrice - minPrice),
                            valueColor = Color.White
                        )
                        TradingViewStatItem(
                            label = "24h Range",
                            value = "${formatCoinPrice(minPrice)} - ${formatCoinPrice(maxPrice)}",
                            valueColor = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF)
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
private fun TradingViewPriceInfo(
    label: String,
    value: Double,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF787B86)
        )
        Text(
            text = formatCoinPrice(value),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun TradingViewStatItem(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF787B86)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

private fun formatVolumeDisplay(range: Double): String {
    return when {
        range >= 1000000 -> "${formatCoinPrice(range / 1000000)}M"
        range >= 1000 -> "${formatCoinPrice(range / 1000)}K"
        else -> formatCoinPrice(range)
    }
}
