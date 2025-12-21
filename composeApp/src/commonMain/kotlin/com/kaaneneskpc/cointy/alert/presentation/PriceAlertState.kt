package com.kaaneneskpc.cointy.alert.presentation

import com.kaaneneskpc.cointy.alert.domain.model.AlertCondition
import com.kaaneneskpc.cointy.alert.domain.model.PriceAlertModel
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import kotlinx.datetime.Instant.Companion.fromEpochMilliseconds
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault

data class PriceAlertState(
    val alerts: List<UiPriceAlertItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreateDialogVisible: Boolean = false,
    val selectedCondition: AlertCondition = AlertCondition.ABOVE,
    val targetPriceInput: String = ""
)

data class UiPriceAlertItem(
    val id: Long,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val targetPriceText: String,
    val conditionText: String,
    val isEnabled: Boolean,
    val isTriggered: Boolean,
    val createdAtText: String,
    val triggeredAtText: String?
)

fun PriceAlertModel.toUiItem(): UiPriceAlertItem {
    val conditionText = when (condition) {
        AlertCondition.ABOVE -> "Above"
        AlertCondition.BELOW -> "Below"
    }
    return UiPriceAlertItem(
        id = id,
        coinId = coinId,
        coinName = coinName,
        coinSymbol = coinSymbol,
        coinIconUrl = coinIconUrl,
        targetPriceText = formatCoinPrice(targetPrice),
        conditionText = conditionText,
        isEnabled = isEnabled,
        isTriggered = isTriggered,
        createdAtText = formatTimestamp(createdAt),
        triggeredAtText = triggeredAt?.let { formatTimestamp(it) }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val date = fromEpochMilliseconds(timestamp)
        .toLocalDateTime(currentSystemDefault())
    return "${date.dayOfMonth}/${date.monthNumber}/${date.year} ${date.hour}:${date.minute.toString().padStart(2, '0')}"
}

