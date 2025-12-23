package com.kaaneneskpc.cointy.alert.data

import kotlinx.datetime.Clock

class VolatilityNotificationTracker {
    private val notifiedCoins: MutableMap<String, Long> = mutableMapOf()
    private val cooldownPeriodMs: Long = 60 * 60 * 1000 // 1 hour cooldown

    fun shouldNotify(coinId: String): Boolean {
        val lastNotified = notifiedCoins[coinId]
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return lastNotified == null || (currentTime - lastNotified) >= cooldownPeriodMs
    }

    fun markAsNotified(coinId: String) {
        notifiedCoins[coinId] = Clock.System.now().toEpochMilliseconds()
    }

    fun clearCoin(coinId: String) {
        notifiedCoins.remove(coinId)
    }

    fun clearAll() {
        notifiedCoins.clear()
    }
}
