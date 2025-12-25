package com.kaaneneskpc.cointy.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.auth.domain.SignInUseCase
import com.kaaneneskpc.cointy.auth.domain.model.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun signIn() {
        val currentState = _state.value
        if (!validateInputs(currentState.email, currentState.password)) {
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = signInUseCase.execute(currentState.email, currentState.password)
            when (result) {
                is AuthResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            user = result.user
                        )
                    }
                }
                is AuthResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun resetLoginSuccess() {
        _state.update { it.copy(isLoginSuccessful = false) }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _state.update { it.copy(errorMessage = "Email is required") }
            return false
        }
        if (!isValidEmail(email)) {
            _state.update { it.copy(errorMessage = "Please enter a valid email") }
            return false
        }
        if (password.isBlank()) {
            _state.update { it.copy(errorMessage = "Password is required") }
            return false
        }
        if (password.length < 6) {
            _state.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}
