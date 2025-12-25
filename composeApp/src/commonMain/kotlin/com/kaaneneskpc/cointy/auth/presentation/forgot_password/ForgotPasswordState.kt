package com.kaaneneskpc.cointy.auth.presentation.forgot_password

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isResetEmailSent: Boolean = false
)
