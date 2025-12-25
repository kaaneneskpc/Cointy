package com.kaaneneskpc.cointy.auth.domain

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute() {
        authRepository.signOut()
    }
}
