package com.kaaneneskpc.cointy.settings.presentation

import com.kaaneneskpc.cointy.settings.domain.model.Currency
import com.kaaneneskpc.cointy.settings.domain.model.Language
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import com.kaaneneskpc.cointy.settings.domain.model.UserProfile

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: Language = Language.ENGLISH,
    val currency: Currency = Currency.USD,
    val userProfile: UserProfile = UserProfile(),
    val isNotificationsEnabled: Boolean = true,
    val isPriceAlertsEnabled: Boolean = true,
    val isVolatilityAlertsEnabled: Boolean = true,
    val volatilityThreshold: Double = 5.0,
    val isOnboardingCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val isEditProfileDialogVisible: Boolean = false,
    val isDataLoaded: Boolean = false
)
