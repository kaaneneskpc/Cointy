package com.kaaneneskpc.cointy.auth.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isEmailVerified: Boolean
)
