package com.kaaneneskpc.cointy.widget

import com.kaaneneskpc.cointy.widget.domain.CoinWidgetData
import com.kaaneneskpc.cointy.widget.domain.PortfolioWidgetData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

@Serializable
data class CoinWidgetDataCodable(
    val id: String,
    val symbol: String,
    val name: String,
    val price: Double,
    val change24h: Double
)

object IosWidgetDataWriter {
    private const val APP_GROUP = "group.com.kaaneneskpc.cointy"
    @OptIn(ExperimentalForeignApi::class)
    fun writePortfolioData(data: PortfolioWidgetData) {
        val sharedDefaults = NSUserDefaults(suiteName = APP_GROUP)
        if (sharedDefaults == null) {
            println("IosWidgetDataWriter: Failed to get shared UserDefaults for App Group")
            return
        }
        println("IosWidgetDataWriter: Writing portfolio data - total: ${data.totalPortfolioValue}, cash: ${data.cashBalance}")
        sharedDefaults.setDouble(data.totalPortfolioValue, forKey = "totalPortfolioValue")
        sharedDefaults.setDouble(data.cashBalance, forKey = "cashBalance")
        sharedDefaults.setDouble(data.lastUpdated.toDouble(), forKey = "lastUpdatedTimestamp")
        sharedDefaults.synchronize()
    }
    @OptIn(ExperimentalForeignApi::class)
    fun writeCoinData(coins: List<CoinWidgetData>) {
        val sharedDefaults = NSUserDefaults(suiteName = APP_GROUP)
        if (sharedDefaults == null) {
            println("IosWidgetDataWriter: Failed to get shared UserDefaults for App Group")
            return
        }
        val codableCoins = coins.map { 
            CoinWidgetDataCodable(
                id = it.coinId,
                symbol = it.symbol,
                name = it.name,
                price = it.price,
                change24h = it.change24h
            )
        }
        val jsonString = Json.encodeToString(codableCoins)
        println("IosWidgetDataWriter: Writing coin data - count: ${coins.size}")
        sharedDefaults.setObject(jsonString, forKey = "widgetCoins")
        sharedDefaults.synchronize()
    }
}

