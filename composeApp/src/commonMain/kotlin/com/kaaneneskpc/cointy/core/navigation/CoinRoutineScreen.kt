package com.kaaneneskpc.cointy.core.navigation

import kotlinx.serialization.Serializable


@Serializable
object Biometric

@Serializable
object Portfolio

@Serializable
object Coins

@Serializable
object TransactionHistory

@Serializable
data class Buy(val coinId: String)

@Serializable
data class Sell(val coinId: String)
