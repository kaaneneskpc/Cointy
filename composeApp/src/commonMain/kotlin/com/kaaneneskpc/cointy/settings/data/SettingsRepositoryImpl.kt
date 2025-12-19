package com.kaaneneskpc.cointy.settings.data

import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {
    override fun getThemeMode(): Flow<ThemeMode> = settingsDataSource.getThemeMode()
    override suspend fun setThemeMode(themeMode: ThemeMode) {
        settingsDataSource.setThemeMode(themeMode)
    }
}

