package com.kaaneneskpc.cointy.settings.domain

import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)
}

