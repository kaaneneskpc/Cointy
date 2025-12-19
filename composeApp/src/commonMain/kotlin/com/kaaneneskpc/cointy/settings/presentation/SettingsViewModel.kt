package com.kaaneneskpc.cointy.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
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
    val state: StateFlow<SettingsState> = combine(
        settingsRepository.getThemeMode(),
        isLoading
    ) { themeMode, loading ->
        SettingsState(
            themeMode = themeMode,
            isLoading = loading
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
}

