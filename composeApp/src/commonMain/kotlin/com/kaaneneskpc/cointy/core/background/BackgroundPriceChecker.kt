package com.kaaneneskpc.cointy.core.background

interface BackgroundPriceChecker {
    fun schedulePeriodicPriceCheck()
    fun cancelPeriodicPriceCheck()
}

expect fun createBackgroundPriceChecker(): BackgroundPriceChecker

