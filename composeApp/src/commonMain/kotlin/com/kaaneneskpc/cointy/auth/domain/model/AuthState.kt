package com.kaaneneskpc.cointy.auth.domain.model

sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    data object Unauthenticated : AuthState
}
