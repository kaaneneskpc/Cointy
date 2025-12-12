package com.kaaneneskpc.cointy.core.util

expect fun formatCoinPrice(amount: Double, showDecimal: Boolean = true): String

expect fun formatCoinPriceUnit(amount: Double, symbol: String): String

expect fun formatCoinPricePercentage(amount: Double): String