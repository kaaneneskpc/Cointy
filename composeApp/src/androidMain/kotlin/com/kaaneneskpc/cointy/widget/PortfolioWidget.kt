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
import com.kaaneneskpc.cointy.widget.domain.GetWidgetDataUseCase
import com.kaaneneskpc.cointy.widget.domain.PortfolioWidgetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color

private val DarkBackground = Color(0xFF1C1C1E)
private val TextGray = Color(0xFF8E8E93)
private val ProfitGreen = Color(0xFF30D158)
private val TextWhite = Color.White

class PortfolioWidget : GlanceAppWidget(), KoinComponent {
    private val getWidgetDataUseCase: GetWidgetDataUseCase by inject()
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetData = withContext(Dispatchers.IO) {
            var portfolioData: PortfolioWidgetData? = null
            getWidgetDataUseCase.execute().onSuccess { data ->
                portfolioData = data.portfolioData
            }
            portfolioData
        }
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(DarkBackground)
                        .padding(16.dp)
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
                            text = "Cointy Portfolio",
                            style = TextStyle(
                                color = ColorProvider(TextWhite),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Image(
                            provider = ImageProvider(R.drawable.ic_refresh),
                            contentDescription = "Refresh",
                            modifier = GlanceModifier
                                .size(24.dp)
                                .clickable(actionRunCallback<RefreshPortfolioWidgetAction>()),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    Text(
                        text = "Total Value",
                        style = TextStyle(
                            color = ColorProvider(TextGray),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = formatCurrency(widgetData?.totalPortfolioValue ?: 0.0),
                        style = TextStyle(
                            color = ColorProvider(ProfitGreen),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Cash Balance",
                                style = TextStyle(
                                    color = ColorProvider(TextGray),
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = formatCurrency(widgetData?.cashBalance ?: 0.0),
                                style = TextStyle(
                                    color = ColorProvider(TextWhite),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            )
                        }
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = formatTime(widgetData?.lastUpdated ?: 0L),
                            style = TextStyle(
                                color = ColorProvider(TextGray),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
    private fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        return formatter.format(amount)
    }
    private fun formatTime(timestamp: Long): String {
        if (timestamp == 0L) return "Not updated"
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "Updated: ${sdf.format(Date(timestamp))}"
    }
}

class RefreshPortfolioWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        PortfolioWidget().updateAll(context)
    }
}
