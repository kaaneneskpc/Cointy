package com.kaaneneskpc.cointy.settings.presentation

import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isLoading: Boolean = false
)

