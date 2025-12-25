package com.kaaneneskpc.cointy.auth.domain

import com.kaaneneskpc.cointy.auth.domain.model.AuthOperationResult
import com.kaaneneskpc.cointy.auth.domain.model.AuthResult
import com.kaaneneskpc.cointy.auth.domain.model.AuthState
import com.kaaneneskpc.cointy.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<AuthState>
    fun getCurrentUser(): Flow<User?>
    fun isUserLoggedIn(): Boolean
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signUpWithEmailAndPassword(email: String, password: String, displayName: String): AuthResult
    suspend fun sendPasswordResetEmail(email: String): AuthOperationResult
    suspend fun signOut()
}
