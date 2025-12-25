package com.kaaneneskpc.cointy.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.auth.domain.SignUpUseCase
import com.kaaneneskpc.cointy.auth.domain.model.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun updateDisplayName(displayName: String) {
        _state.update { it.copy(displayName = displayName, errorMessage = null) }
    }

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun signUp() {
        val currentState = _state.value
        if (!validateInputs(currentState)) {
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = signUpUseCase.execute(
                email = currentState.email,
                password = currentState.password,
                displayName = currentState.displayName
            )
            when (result) {
                is AuthResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRegistrationSuccessful = true
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

    fun resetRegistrationSuccess() {
        _state.update { it.copy(isRegistrationSuccessful = false) }
    }

    private fun validateInputs(state: RegisterState): Boolean {
        if (state.displayName.isBlank()) {
            _state.update { it.copy(errorMessage = "Name is required") }
            return false
        }
        if (state.email.isBlank()) {
            _state.update { it.copy(errorMessage = "Email is required") }
            return false
        }
        if (!isValidEmail(state.email)) {
            _state.update { it.copy(errorMessage = "Please enter a valid email") }
            return false
        }
        if (state.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Password is required") }
            return false
        }
        if (state.password.length < 6) {
            _state.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return false
        }
        if (state.password != state.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match") }
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}
