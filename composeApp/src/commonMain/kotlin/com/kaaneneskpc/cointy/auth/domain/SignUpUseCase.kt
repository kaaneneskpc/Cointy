package com.kaaneneskpc.cointy.auth.domain

import com.kaaneneskpc.cointy.auth.domain.model.AuthResult

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String, displayName: String): AuthResult {
        return authRepository.signUpWithEmailAndPassword(email, password, displayName)
    }
}
