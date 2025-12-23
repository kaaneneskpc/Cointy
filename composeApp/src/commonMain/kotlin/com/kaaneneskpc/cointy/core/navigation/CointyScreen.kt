package com.kaaneneskpc.cointy.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Biometric

@Serializable
object Onboarding

@Serializable
object Portfolio

@Serializable
object Coins

@Serializable
object TransactionHistory

@Serializable
object Analytics

@Serializable
object PriceAlerts

@Serializable
data class CreateAlert(val coinId: String)

@Serializable
data class Buy(val coinId: String)

@Serializable
data class Sell(val coinId: String)

@Serializable
object Settings

@Serializable
object Export

@Serializable
object RiskAnalysis
