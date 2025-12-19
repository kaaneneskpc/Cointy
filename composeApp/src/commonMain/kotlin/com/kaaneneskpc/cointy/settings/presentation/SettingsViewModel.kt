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
    val state: StateFlow<SettingsState> = combine(
        settingsRepository.getThemeMode(),
        settingsRepository.getLanguage(),
        settingsRepository.getCurrency(),
        settingsRepository.getUserProfile(),
        settingsRepository.getNotificationsEnabled(),
        settingsRepository.getPriceAlertsEnabled(),
        isLoading,
        isEditProfileDialogVisible
    ) { values ->
        SettingsState(
            themeMode = values[0] as ThemeMode,
            language = values[1] as Language,
            currency = values[2] as Currency,
            userProfile = values[3] as UserProfile,
            isNotificationsEnabled = values[4] as Boolean,
            isPriceAlertsEnabled = values[5] as Boolean,
            isLoading = values[6] as Boolean,
            isEditProfileDialogVisible = values[7] as Boolean
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
    fun showEditProfileDialog() {
        isEditProfileDialogVisible.value = true
    }
    fun hideEditProfileDialog() {
        isEditProfileDialogVisible.value = false
    }
}
