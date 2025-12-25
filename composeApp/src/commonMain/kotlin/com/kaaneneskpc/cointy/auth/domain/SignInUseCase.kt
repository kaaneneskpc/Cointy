package com.kaaneneskpc.cointy.auth.domain

import com.kaaneneskpc.cointy.auth.domain.model.AuthResult

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): AuthResult {
        return authRepository.signInWithEmailAndPassword(email, password)
    }
}
