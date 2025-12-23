package com.kaaneneskpc.cointy.settings.data

import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import com.kaaneneskpc.cointy.settings.domain.model.Currency
import com.kaaneneskpc.cointy.settings.domain.model.Language
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import com.kaaneneskpc.cointy.settings.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {
    override fun getThemeMode(): Flow<ThemeMode> = settingsDataSource.getThemeMode()
    override suspend fun setThemeMode(themeMode: ThemeMode) {
        settingsDataSource.setThemeMode(themeMode)
    }
    override fun getLanguage(): Flow<Language> = settingsDataSource.getLanguage()
    override suspend fun setLanguage(language: Language) {
        settingsDataSource.setLanguage(language)
    }
    override fun getCurrency(): Flow<Currency> = settingsDataSource.getCurrency()
    override suspend fun setCurrency(currency: Currency) {
        settingsDataSource.setCurrency(currency)
    }
    override fun getUserProfile(): Flow<UserProfile> = settingsDataSource.getUserProfile()
    override suspend fun setUserProfile(userProfile: UserProfile) {
        settingsDataSource.setUserProfile(userProfile)
    }
    override fun getNotificationsEnabled(): Flow<Boolean> = settingsDataSource.getNotificationsEnabled()
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settingsDataSource.setNotificationsEnabled(enabled)
    }
    override fun getPriceAlertsEnabled(): Flow<Boolean> = settingsDataSource.getPriceAlertsEnabled()
    override suspend fun setPriceAlertsEnabled(enabled: Boolean) {
        settingsDataSource.setPriceAlertsEnabled(enabled)
    }
    override fun getVolatilityAlertsEnabled(): Flow<Boolean> = settingsDataSource.getVolatilityAlertsEnabled()
    override suspend fun setVolatilityAlertsEnabled(enabled: Boolean) {
        settingsDataSource.setVolatilityAlertsEnabled(enabled)
    }
    override fun getVolatilityThreshold(): Flow<Double> = settingsDataSource.getVolatilityThreshold()
    override suspend fun setVolatilityThreshold(threshold: Double) {
        settingsDataSource.setVolatilityThreshold(threshold)
    }

    override fun isOnboardingCompleted(): Flow<Boolean> = settingsDataSource.isOnboardingCompleted()

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settingsDataSource.setOnboardingCompleted(completed)
    }
}
