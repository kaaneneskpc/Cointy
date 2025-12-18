package com.kaaneneskpc.cointy.alert.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.alert.domain.CreatePriceAlertUseCase
import com.kaaneneskpc.cointy.alert.domain.DeletePriceAlertUseCase
import com.kaaneneskpc.cointy.alert.domain.GetPriceAlertsUseCase
import com.kaaneneskpc.cointy.alert.domain.TogglePriceAlertUseCase
import com.kaaneneskpc.cointy.alert.domain.model.AlertCondition
import com.kaaneneskpc.cointy.core.domain.coin.Coin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PriceAlertViewModel(
    private val getPriceAlertsUseCase: GetPriceAlertsUseCase,
    private val createPriceAlertUseCase: CreatePriceAlertUseCase,
    private val deletePriceAlertUseCase: DeletePriceAlertUseCase,
    private val togglePriceAlertUseCase: TogglePriceAlertUseCase
) : ViewModel() {
    private val localState = MutableStateFlow(PriceAlertState())
    private val alertsFlow = getPriceAlertsUseCase.executeAll()
    val state = combine(localState, alertsFlow) { local, alerts ->
        local.copy(
            alerts = alerts.map { it.toUiItem() },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PriceAlertState(isLoading = true)
    )
    fun showCreateDialog() {
        localState.update { it.copy(isCreateDialogVisible = true) }
    }
    fun hideCreateDialog() {
        localState.update {
            it.copy(
                isCreateDialogVisible = false,
                targetPriceInput = "",
                selectedCondition = AlertCondition.ABOVE
            )
        }
    }
    fun updateTargetPrice(price: String) {
        localState.update { it.copy(targetPriceInput = price) }
    }
    fun updateCondition(condition: AlertCondition) {
        localState.update { it.copy(selectedCondition = condition) }
    }
    fun createAlert(coin: Coin) {
        viewModelScope.launch {
            val targetPrice = localState.value.targetPriceInput.toDoubleOrNull() ?: return@launch
            createPriceAlertUseCase.execute(
                coin = coin,
                targetPrice = targetPrice,
                condition = localState.value.selectedCondition
            )
            hideCreateDialog()
        }
    }
    fun deleteAlert(alertId: Long) {
        viewModelScope.launch {
            deletePriceAlertUseCase.execute(alertId)
        }
    }
    fun toggleAlert(alertId: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            togglePriceAlertUseCase.execute(alertId, isEnabled)
        }
    }
}

