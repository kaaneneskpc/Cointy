package com.kaaneneskpc.cointy.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.ImageProvider
import androidx.glance.Image
import androidx.glance.layout.ContentScale
import androidx.glance.unit.ColorProvider
import com.kaaneneskpc.cointy.MainActivity
import com.kaaneneskpc.cointy.R
import com.kaaneneskpc.cointy.core.domain.onSuccess
import com.kaaneneskpc.cointy.widget.domain.CoinWidgetData
import com.kaaneneskpc.cointy.widget.domain.GetWidgetDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.graphics.Color

private val DarkBackground = Color(0xFF1C1C1E)
private val TextGray = Color(0xFF8E8E93)
private val ProfitGreen = Color(0xFF30D158)
private val LossRed = Color(0xFFFF453A)
private val TextWhite = Color.White

class CoinPriceWidget : GlanceAppWidget(), KoinComponent {
    private val getWidgetDataUseCase: GetWidgetDataUseCase by inject()
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val coins = withContext(Dispatchers.IO) {
            var coinList: List<CoinWidgetData> = emptyList()
            getWidgetDataUseCase.execute().onSuccess { data ->
                coinList = data.coins.take(5)
            }
            coinList
        }
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(DarkBackground)
                        .padding(12.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Coin Prices",
                            style = TextStyle(
                                color = ColorProvider(TextWhite),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Image(
                            provider = ImageProvider(R.drawable.ic_refresh),
                            contentDescription = "Refresh",
                            modifier = GlanceModifier
                                .size(24.dp)
                                .clickable(actionRunCallback<RefreshCoinPriceWidgetAction>()),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    if (coins.isEmpty()) {
                        Column(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No coins in portfolio",
                                style = TextStyle(
                                    color = ColorProvider(TextGray),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    } else {
                        LazyColumn {
                            items(coins) { coin ->
                                CoinRow(coin)
                            }
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun CoinRow(coin: CoinWidgetData) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = GlanceModifier.width(80.dp)
        ) {
            Text(
                text = coin.symbol,
                style = TextStyle(
                    color = ColorProvider(TextWhite),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = coin.name.take(12),
                style = TextStyle(
                    color = ColorProvider(TextGray),
                    fontSize = 10.sp
                )
            )
        }
        Spacer(modifier = GlanceModifier.defaultWeight())
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatCurrency(coin.price),
                style = TextStyle(
                    color = ColorProvider(TextWhite),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
            Text(
                text = formatChange(coin.change24h),
                style = TextStyle(
                    color = ColorProvider(if (coin.change24h >= 0) ProfitGreen else LossRed),
                    fontSize = 10.sp
                )
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(amount)
}

private fun formatChange(change: Double): String {
    val sign = if (change >= 0) "+" else ""
    return "$sign${String.format(Locale.US, "%.2f", change)}%"
}

class RefreshCoinPriceWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        CoinPriceWidget().updateAll(context)
    }
}
