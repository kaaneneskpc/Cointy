package com.kaaneneskpc.cointy.settings.data

import com.kaaneneskpc.cointy.settings.domain.model.Currency
import com.kaaneneskpc.cointy.settings.domain.model.Language
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import com.kaaneneskpc.cointy.settings.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySettingsDataSource : SettingsDataSource {
    private val themeModeFlow: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.SYSTEM)
    private val languageFlow: MutableStateFlow<Language> = MutableStateFlow(Language.ENGLISH)
    private val currencyFlow: MutableStateFlow<Currency> = MutableStateFlow(Currency.USD)
    private val userProfileFlow: MutableStateFlow<UserProfile> = MutableStateFlow(UserProfile())
    private val notificationsEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val priceAlertsEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val volatilityAlertsEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val volatilityThresholdFlow: MutableStateFlow<Double> = MutableStateFlow(5.0)
    private val onboardingCompletedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun getThemeMode(): Flow<ThemeMode> = themeModeFlow.asStateFlow()
    override suspend fun setThemeMode(themeMode: ThemeMode) {
        themeModeFlow.value = themeMode
    }
    override fun getLanguage(): Flow<Language> = languageFlow.asStateFlow()
    override suspend fun setLanguage(language: Language) {
        languageFlow.value = language
    }
    override fun getCurrency(): Flow<Currency> = currencyFlow.asStateFlow()
    override suspend fun setCurrency(currency: Currency) {
        currencyFlow.value = currency
    }
    override fun getUserProfile(): Flow<UserProfile> = userProfileFlow.asStateFlow()
    override suspend fun setUserProfile(userProfile: UserProfile) {
        userProfileFlow.value = userProfile
    }
    override fun getNotificationsEnabled(): Flow<Boolean> = notificationsEnabledFlow.asStateFlow()
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        notificationsEnabledFlow.value = enabled
    }
    override fun getPriceAlertsEnabled(): Flow<Boolean> = priceAlertsEnabledFlow.asStateFlow()
    override suspend fun setPriceAlertsEnabled(enabled: Boolean) {
        priceAlertsEnabledFlow.value = enabled
    }
    override fun getVolatilityAlertsEnabled(): Flow<Boolean> = volatilityAlertsEnabledFlow.asStateFlow()
    override suspend fun setVolatilityAlertsEnabled(enabled: Boolean) {
        volatilityAlertsEnabledFlow.value = enabled
    }
    override fun getVolatilityThreshold(): Flow<Double> = volatilityThresholdFlow.asStateFlow()
    override suspend fun setVolatilityThreshold(threshold: Double) {
        volatilityThresholdFlow.value = threshold
    }

    override fun isOnboardingCompleted(): Flow<Boolean> = onboardingCompletedFlow.asStateFlow()

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingCompletedFlow.value = completed
    }
}
