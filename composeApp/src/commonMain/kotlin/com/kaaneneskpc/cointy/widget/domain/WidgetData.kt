package com.kaaneneskpc.cointy.widget.domain

data class PortfolioWidgetData(
    val totalPortfolioValue: Double,
    val cashBalance: Double,
    val lastUpdated: Long
)

data class CoinWidgetData(
    val coinId: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val price: Double,
    val change24h: Double
)

data class WidgetData(
    val portfolioData: PortfolioWidgetData,
    val coins: List<CoinWidgetData>
)
