package com.kaaneneskpc.cointy.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state: MutableStateFlow<OnboardingState> = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onPageChanged(index: Int) {
        _state.update { it.copy(currentPageIndex = index) }
    }

    fun onNextPage() {
        val currentIndex = _state.value.currentPageIndex
        if (currentIndex < 2) {
            _state.update { it.copy(currentPageIndex = currentIndex + 1) }
        } else {
            completeOnboarding()
        }
    }

    fun onSkip() {
        completeOnboarding()
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
            _state.update { it.copy(isCompleted = true) }
        }
    }
}

