package com.kaaneneskpc.cointy.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import com.kaaneneskpc.cointy.settings.domain.model.Currency
import com.kaaneneskpc.cointy.settings.domain.model.Language
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import com.kaaneneskpc.cointy.settings.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isEditProfileDialogVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val settingsFlow1 = combine(
        settingsRepository.getThemeMode(),
        settingsRepository.getLanguage(),
        settingsRepository.getCurrency(),
        settingsRepository.getUserProfile(),
        settingsRepository.getNotificationsEnabled()
    ) { themeMode, language, currency, userProfile, notificationsEnabled ->
        SettingsFlowData1(themeMode, language, currency, userProfile, notificationsEnabled)
    }

    private val settingsFlow2 = combine(
        settingsRepository.getPriceAlertsEnabled(),
        settingsRepository.getVolatilityAlertsEnabled(),
        settingsRepository.getVolatilityThreshold(),
        settingsRepository.isOnboardingCompleted(),
        isLoading,
        isEditProfileDialogVisible
    ) { values ->
        SettingsFlowData2(
            priceAlertsEnabled = values[0] as Boolean,
            volatilityAlertsEnabled = values[1] as Boolean,
            volatilityThreshold = values[2] as Double,
            onboardingCompleted = values[3] as Boolean,
            isLoading = values[4] as Boolean,
            isEditProfileDialogVisible = values[5] as Boolean
        )
    }

    val state: StateFlow<SettingsState> = combine(settingsFlow1, settingsFlow2) { data1, data2 ->
        SettingsState(
            themeMode = data1.themeMode,
            language = data1.language,
            currency = data1.currency,
            userProfile = data1.userProfile,
            isNotificationsEnabled = data1.notificationsEnabled,
            isPriceAlertsEnabled = data2.priceAlertsEnabled,
            isVolatilityAlertsEnabled = data2.volatilityAlertsEnabled,
            volatilityThreshold = data2.volatilityThreshold,
            isOnboardingCompleted = data2.onboardingCompleted,
            isLoading = data2.isLoading,
            isEditProfileDialogVisible = data2.isEditProfileDialogVisible,
            isDataLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsState()
    )

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }
    fun updateLanguage(language: Language) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }
    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            settingsRepository.setCurrency(currency)
        }
    }
    fun updateUserProfile(userProfile: UserProfile) {
        viewModelScope.launch {
            settingsRepository.setUserProfile(userProfile)
        }
    }
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }
    fun updatePriceAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPriceAlertsEnabled(enabled)
        }
    }
    fun updateVolatilityAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setVolatilityAlertsEnabled(enabled)
        }
    }
    fun updateVolatilityThreshold(threshold: Double) {
        viewModelScope.launch {
            settingsRepository.setVolatilityThreshold(threshold)
        }
    }
    fun showEditProfileDialog() {
        isEditProfileDialogVisible.value = true
    }
    fun hideEditProfileDialog() {
        isEditProfileDialogVisible.value = false
    }
}

private data class SettingsFlowData1(
    val themeMode: ThemeMode,
    val language: Language,
    val currency: Currency,
    val userProfile: UserProfile,
    val notificationsEnabled: Boolean
)

private data class SettingsFlowData2(
    val priceAlertsEnabled: Boolean,
    val volatilityAlertsEnabled: Boolean,
    val volatilityThreshold: Double,
    val onboardingCompleted: Boolean,
    val isLoading: Boolean,
    val isEditProfileDialogVisible: Boolean
)

