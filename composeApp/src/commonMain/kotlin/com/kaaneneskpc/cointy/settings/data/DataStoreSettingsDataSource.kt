package com.kaaneneskpc.cointy.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaaneneskpc.cointy.settings.domain.model.Currency
import com.kaaneneskpc.cointy.settings.domain.model.Language
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import com.kaaneneskpc.cointy.settings.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsDataSource(
    private val dataStore: DataStore<Preferences>
) : SettingsDataSource {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
        val USER_FIRST_NAME = stringPreferencesKey("user_first_name")
        val USER_LAST_NAME = stringPreferencesKey("user_last_name")
        val USER_PROFILE_PHOTO_URI = stringPreferencesKey("user_profile_photo_uri")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val PRICE_ALERTS_ENABLED = booleanPreferencesKey("price_alerts_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
    override fun getThemeMode(): Flow<ThemeMode> = dataStore.data.map { preferences ->
        val themeModeString = preferences[PreferencesKeys.THEME_MODE]
        themeModeString?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM
    }
    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }
    override fun getLanguage(): Flow<Language> = dataStore.data.map { preferences ->
        val languageString = preferences[PreferencesKeys.LANGUAGE]
        languageString?.let { Language.valueOf(it) } ?: Language.ENGLISH
    }
    override suspend fun setLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }
    override fun getCurrency(): Flow<Currency> = dataStore.data.map { preferences ->
        val currencyString = preferences[PreferencesKeys.CURRENCY]
        currencyString?.let { Currency.valueOf(it) } ?: Currency.USD
    }
    override suspend fun setCurrency(currency: Currency) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY] = currency.name
        }
    }
    override fun getUserProfile(): Flow<UserProfile> = dataStore.data.map { preferences ->
        UserProfile(
            firstName = preferences[PreferencesKeys.USER_FIRST_NAME] ?: "",
            lastName = preferences[PreferencesKeys.USER_LAST_NAME] ?: "",
            profilePhotoUri = preferences[PreferencesKeys.USER_PROFILE_PHOTO_URI]
        )
    }
    override suspend fun setUserProfile(userProfile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_FIRST_NAME] = userProfile.firstName
            preferences[PreferencesKeys.USER_LAST_NAME] = userProfile.lastName
            if (userProfile.profilePhotoUri != null) {
                preferences[PreferencesKeys.USER_PROFILE_PHOTO_URI] = userProfile.profilePhotoUri
            } else {
                preferences.remove(PreferencesKeys.USER_PROFILE_PHOTO_URI)
            }
        }
    }
    override fun getNotificationsEnabled(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }
    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    override fun getPriceAlertsEnabled(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PRICE_ALERTS_ENABLED] ?: true
    }
    override suspend fun setPriceAlertsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRICE_ALERTS_ENABLED] = enabled
        }
    }
    override fun isOnboardingCompleted(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }
    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}

