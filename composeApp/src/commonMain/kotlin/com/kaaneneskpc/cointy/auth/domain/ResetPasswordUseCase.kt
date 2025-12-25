package com.kaaneneskpc.cointy.auth.domain

import com.kaaneneskpc.cointy.auth.domain.model.AuthOperationResult

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String): AuthOperationResult {
        return authRepository.sendPasswordResetEmail(email)
    }
}
