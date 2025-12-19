package com.kaaneneskpc.cointy.settings.data

import com.kaaneneskpc.cointy.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySettingsDataSource : SettingsDataSource {
    private val themeModeFlow: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.SYSTEM)
    override fun getThemeMode(): Flow<ThemeMode> = themeModeFlow.asStateFlow()
    override suspend fun setThemeMode(themeMode: ThemeMode) {
        themeModeFlow.value = themeMode
    }
}

