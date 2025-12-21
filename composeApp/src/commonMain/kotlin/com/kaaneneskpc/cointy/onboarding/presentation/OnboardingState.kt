package com.kaaneneskpc.cointy.onboarding.presentation

data class OnboardingState(
    val currentPageIndex: Int = 0,
    val isCompleted: Boolean = false
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: String
)

