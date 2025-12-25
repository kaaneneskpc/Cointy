package com.kaaneneskpc.cointy.auth.data

import com.kaaneneskpc.cointy.auth.domain.AuthRepository
import com.kaaneneskpc.cointy.auth.domain.model.AuthOperationResult
import com.kaaneneskpc.cointy.auth.domain.model.AuthResult
import com.kaaneneskpc.cointy.auth.domain.model.AuthState
import com.kaaneneskpc.cointy.auth.domain.model.User
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun getAuthState(): Flow<AuthState> {
        return firebaseAuth.authStateChanged.map { firebaseUser ->
            if (firebaseUser != null) {
                AuthState.Authenticated(firebaseUser.toDomainUser())
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return firebaseAuth.authStateChanged.map { firebaseUser ->
            firebaseUser?.toDomainUser()
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                AuthResult.Success(user.toDomainUser())
            } else {
                AuthResult.Error("Authentication failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String
    ): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val user = result.user
            if (user != null) {
                user.updateProfile(displayName = displayName)
                AuthResult.Success(user.toDomainUser().copy(displayName = displayName))
            } else {
                AuthResult.Error("Registration failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthOperationResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email)
            AuthOperationResult.Success
        } catch (e: Exception) {
            AuthOperationResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toDomainUser(): User {
        return User(
            uid = uid,
            email = email ?: "",
            displayName = displayName,
            isEmailVerified = isEmailVerified
        )
    }
}
