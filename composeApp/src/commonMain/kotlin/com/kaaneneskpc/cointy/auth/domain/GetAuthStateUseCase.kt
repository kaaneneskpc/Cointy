package com.kaaneneskpc.cointy.auth.domain

import com.kaaneneskpc.cointy.auth.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

class GetAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    fun execute(): Flow<AuthState> {
        return authRepository.getAuthState()
    }
}
