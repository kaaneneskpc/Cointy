package com.kaaneneskpc.cointy.auth.presentation.login

import com.kaaneneskpc.cointy.auth.domain.model.User

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val user: User? = null
)
