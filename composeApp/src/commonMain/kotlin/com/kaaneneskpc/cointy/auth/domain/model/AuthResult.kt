package com.kaaneneskpc.cointy.auth.domain.model

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Error(val message: String) : AuthResult
}

sealed interface AuthOperationResult {
    data object Success : AuthOperationResult
    data class Error(val message: String) : AuthOperationResult
}
