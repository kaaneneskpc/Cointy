package com.kaaneneskpc.cointy.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.auth.domain.ResetPasswordUseCase
import com.kaaneneskpc.cointy.auth.domain.model.AuthOperationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<ForgotPasswordState> = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun resetPassword() {
        val email = _state.value.email
        if (!validateEmail(email)) {
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = resetPasswordUseCase.execute(email)
            when (result) {
                is AuthOperationResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isResetEmailSent = true
                        )
                    }
                }
                is AuthOperationResult.Error -> {
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

    fun resetState() {
        _state.update { ForgotPasswordState() }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            _state.update { it.copy(errorMessage = "Email is required") }
            return false
        }
        if (!isValidEmail(email)) {
            _state.update { it.copy(errorMessage = "Please enter a valid email") }
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}
