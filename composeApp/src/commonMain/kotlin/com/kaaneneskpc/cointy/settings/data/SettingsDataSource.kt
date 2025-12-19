package com.kaaneneskpc.cointy.settings.data

import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)
}

