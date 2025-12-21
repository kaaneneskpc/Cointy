package com.kaaneneskpc.cointy.export.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaaneneskpc.cointy.core.localization.LocalStringResources
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.theme.CointyColors
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExportViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val strings = LocalStringResources.current
    LaunchedEffect(state.isExportSuccess) {
        if (state.isExportSuccess) {
            delay(3000)
            viewModel.clearExportStatus()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.exportPortfolio,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ExportPreviewCard(
                portfolioCoinCount = state.portfolioCoinCount,
                transactionCount = state.transactionCount,
                totalPortfolioValue = state.totalPortfolioValue,
                cashBalance = state.cashBalance,
                strings = strings
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = strings.exportSelectFormat,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormatSelectionCard(
                    format = ExportFormat.CSV,
                    isSelected = state.selectedFormat == ExportFormat.CSV,
                    onClick = { viewModel.selectFormat(ExportFormat.CSV) },
                    modifier = Modifier.weight(1f),
                    strings = strings
                )
                FormatSelectionCard(
                    format = ExportFormat.JSON,
                    isSelected = state.selectedFormat == ExportFormat.JSON,
                    onClick = { viewModel.selectFormat(ExportFormat.JSON) },
                    modifier = Modifier.weight(1f),
                    strings = strings
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.exportData() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CointyColors.Primary
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = strings.exportButton,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = state.isExportSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExportSuccessCard(
                    fileName = state.exportedFileName ?: "",
                    strings = strings
                )
            }
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExportErrorCard(
                    errorMessage = state.errorMessage ?: "",
                    onDismiss = { viewModel.clearExportStatus() },
                    strings = strings
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExportPreviewCard(
    portfolioCoinCount: Int,
    transactionCount: Int,
    totalPortfolioValue: Double,
    cashBalance: Double,
    strings: com.kaaneneskpc.cointy.core.localization.StringResources
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = strings.exportPreview,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PreviewItem(
                    label = strings.totalValue,
                    value = formatCoinPrice(totalPortfolioValue)
                )
                PreviewItem(
                    label = strings.cashBalance,
                    value = formatCoinPrice(cashBalance)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PreviewItem(
                    label = strings.exportCoinsCount,
                    value = portfolioCoinCount.toString()
                )
                PreviewItem(
                    label = strings.exportTransactionsCount,
                    value = transactionCount.toString()
                )
            }
        }
    }
}

@Composable
private fun PreviewItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FormatSelectionCard(
    format: ExportFormat,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    strings: com.kaaneneskpc.cointy.core.localization.StringResources
) {
    val borderColor = if (isSelected) CointyColors.Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) CointyColors.Primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isSelected) CointyColors.Primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = format.extension.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when (format) {
                    ExportFormat.CSV -> strings.exportCsv
                    ExportFormat.JSON -> strings.exportJson
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = ".${format.extension}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CointyColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ExportSuccessCard(
    fileName: String,
    strings: com.kaaneneskpc.cointy.core.localization.StringResources
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CointyColors.Profit.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(CointyColors.Profit, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = strings.exportSuccess,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CointyColors.Profit
                )
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = strings.exportFileSaved,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ExportErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit,
    strings: com.kaaneneskpc.cointy.core.localization.StringResources
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CointyColors.Loss.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(CointyColors.Loss, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.exportError,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CointyColors.Loss
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

