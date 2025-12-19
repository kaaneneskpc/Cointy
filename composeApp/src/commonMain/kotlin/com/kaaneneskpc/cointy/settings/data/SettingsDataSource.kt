package com.kaaneneskpc.cointy.settings.data

import com.kaaneneskpc.cointy.settings.domain.model.Currency
import com.kaaneneskpc.cointy.settings.domain.model.Language
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import com.kaaneneskpc.cointy.settings.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)
    fun getLanguage(): Flow<Language>
    suspend fun setLanguage(language: Language)
    fun getCurrency(): Flow<Currency>
    suspend fun setCurrency(currency: Currency)
    fun getUserProfile(): Flow<UserProfile>
    suspend fun setUserProfile(userProfile: UserProfile)
    fun getNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)
    fun getPriceAlertsEnabled(): Flow<Boolean>
    suspend fun setPriceAlertsEnabled(enabled: Boolean)
}
