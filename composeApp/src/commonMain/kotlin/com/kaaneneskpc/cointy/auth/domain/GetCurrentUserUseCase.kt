package com.kaaneneskpc.cointy.auth.domain

import com.kaaneneskpc.cointy.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    fun execute(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}
