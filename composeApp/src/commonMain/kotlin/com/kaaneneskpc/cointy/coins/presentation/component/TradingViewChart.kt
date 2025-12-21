package com.kaaneneskpc.cointy.coins.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaaneneskpc.cointy.core.util.formatCoinPrice
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ChartDataPoint(
    val price: Double,
    val timestamp: Long
)

@Composable
fun TradingViewChart(
    modifier: Modifier = Modifier,
    dataPoints: List<ChartDataPoint>,
    profitColor: Color,
    lossColor: Color,
    backgroundColor: Color = Color(0xFF131722),
    gridColor: Color = Color(0xFF2A2E39),
    textColor: Color = Color(0xFF787B86),
    crosshairColor: Color = Color(0xFF9598A1),
    onCrosshairMove: (ChartDataPoint?) -> Unit = {}
) {
    if (dataPoints.isEmpty()) return
    val sortedData = remember(dataPoints) { dataPoints.sortedBy { it.timestamp } }
    val maxPrice = remember(sortedData) { sortedData.maxOf { it.price } }
    val minPrice = remember(sortedData) { sortedData.minOf { it.price } }
    val priceRange = maxPrice - minPrice
    val isPositive = sortedData.last().price >= sortedData.first().price
    val lineColor = if (isPositive) profitColor else lossColor
    val animationProgress = remember { Animatable(0f) }
    var crosshairPosition by remember { mutableStateOf<Offset?>(null) }
    var selectedDataPoint by remember { mutableStateOf<ChartDataPoint?>(null) }
    val textMeasurer = rememberTextMeasurer()
    LaunchedEffect(sortedData) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }
    Column(modifier = modifier) {
        if (selectedDataPoint != null) {
            CrosshairInfoBar(
                dataPoint = selectedDataPoint!!,
                isPositive = isPositive,
                profitColor = profitColor,
                lossColor = lossColor,
                textColor = textColor
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .pointerInput(sortedData) {
                    detectTapGestures(
                        onPress = { offset ->
                            crosshairPosition = offset
                            val dataIndex = calculateDataIndex(offset.x, size.width.toFloat(), sortedData.size)
                            selectedDataPoint = sortedData.getOrNull(dataIndex)
                            onCrosshairMove(selectedDataPoint)
                        },
                        onTap = {
                            crosshairPosition = null
                            selectedDataPoint = null
                            onCrosshairMove(null)
                        }
                    )
                }
                .pointerInput(sortedData) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            crosshairPosition = change.position
                            val dataIndex = calculateDataIndex(change.position.x, size.width.toFloat(), sortedData.size)
                            selectedDataPoint = sortedData.getOrNull(dataIndex)
                            onCrosshairMove(selectedDataPoint)
                        },
                        onDragEnd = {
                            crosshairPosition = null
                            selectedDataPoint = null
                            onCrosshairMove(null)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(start = 8.dp, end = 60.dp, top = 24.dp, bottom = 32.dp)) {
                val chartWidth = size.width
                val chartHeight = size.height
                drawPriceGrid(
                    chartWidth = chartWidth,
                    chartHeight = chartHeight,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    gridColor = gridColor,
                    textColor = textColor,
                    textMeasurer = textMeasurer
                )
                drawTimeGrid(
                    chartWidth = chartWidth,
                    chartHeight = chartHeight,
                    dataPoints = sortedData,
                    gridColor = gridColor,
                    textColor = textColor,
                    textMeasurer = textMeasurer
                )
                val animatedDataCount = (sortedData.size * animationProgress.value).toInt().coerceAtLeast(2)
                val animatedData = sortedData.take(animatedDataCount)
                val points = animatedData.mapIndexed { index, point ->
                    val x = (index.toFloat() / (sortedData.size - 1).coerceAtLeast(1)) * chartWidth
                    val normalizedPrice = if (priceRange > 0) (point.price - minPrice) / priceRange else 0.5
                    val y = chartHeight * (1 - normalizedPrice.toFloat())
                    Offset(x, y)
                }
                if (points.size >= 2) {
                    drawGradientFill(points, chartHeight, lineColor)
                    drawSmoothLine(points, lineColor)
                }
                drawCurrentPriceLine(
                    chartWidth = chartWidth,
                    chartHeight = chartHeight,
                    currentPrice = sortedData.last().price,
                    minPrice = minPrice,
                    priceRange = priceRange,
                    lineColor = lineColor,
                    textMeasurer = textMeasurer
                )
                crosshairPosition?.let { position ->
                    val adjustedX = (position.x - 8.dp.toPx()).coerceIn(0f, chartWidth)
                    val adjustedY = (position.y - 24.dp.toPx()).coerceIn(0f, chartHeight)
                    drawCrosshair(
                        position = Offset(adjustedX, adjustedY),
                        chartWidth = chartWidth,
                        chartHeight = chartHeight,
                        crosshairColor = crosshairColor
                    )
                }
            }
            Canvas(modifier = Modifier.fillMaxSize().padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 32.dp)) {
                val chartWidth = size.width - 52.dp.toPx()
                drawPriceAxis(
                    chartWidth = chartWidth,
                    chartHeight = size.height,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    textColor = textColor,
                    textMeasurer = textMeasurer
                )
            }
        }
    }
}

@Composable
private fun CrosshairInfoBar(
    dataPoint: ChartDataPoint,
    isPositive: Boolean,
    profitColor: Color,
    lossColor: Color,
    textColor: Color
) {
    val dateTime = remember(dataPoint.timestamp) {
        val instant = Instant.fromEpochSeconds(dataPoint.timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.dayOfMonth.toString().padStart(2, '0')}/${localDateTime.monthNumber.toString().padStart(2, '0')} ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateTime,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Price: ",
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
        Text(
            text = formatCoinPrice(dataPoint.price),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) profitColor else lossColor
        )
    }
}

private fun calculateDataIndex(xPosition: Float, chartWidth: Float, dataSize: Int): Int {
    val adjustedX = xPosition - 8f
    val ratio = (adjustedX / (chartWidth - 68f)).coerceIn(0f, 1f)
    return (ratio * (dataSize - 1)).toInt().coerceIn(0, dataSize - 1)
}

private fun DrawScope.drawPriceGrid(
    chartWidth: Float,
    chartHeight: Float,
    minPrice: Double,
    maxPrice: Double,
    gridColor: Color,
    textColor: Color,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val gridLineCount = 5
    val priceRange = maxPrice - minPrice
    for (i in 0..gridLineCount) {
        val y = (chartHeight / gridLineCount) * i
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(chartWidth, y),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
        )
    }
}

private fun DrawScope.drawTimeGrid(
    chartWidth: Float,
    chartHeight: Float,
    dataPoints: List<ChartDataPoint>,
    gridColor: Color,
    textColor: Color,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val timeLabels = 6
    val step = (dataPoints.size - 1) / (timeLabels - 1).coerceAtLeast(1)
    for (i in 0 until timeLabels) {
        val dataIndex = (i * step).coerceIn(0, dataPoints.size - 1)
        val x = (dataIndex.toFloat() / (dataPoints.size - 1).coerceAtLeast(1)) * chartWidth
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, chartHeight),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
        )
        val timestamp = dataPoints[dataIndex].timestamp
        val instant = Instant.fromEpochSeconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val timeText = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
        val textLayoutResult = textMeasurer.measure(
            text = timeText,
            style = TextStyle(fontSize = 9.sp, color = textColor)
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(x - textLayoutResult.size.width / 2, chartHeight + 8f)
        )
    }
}

private fun DrawScope.drawPriceAxis(
    chartWidth: Float,
    chartHeight: Float,
    minPrice: Double,
    maxPrice: Double,
    textColor: Color,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val gridLineCount = 5
    val priceRange = maxPrice - minPrice
    for (i in 0..gridLineCount) {
        val y = (chartHeight / gridLineCount) * i
        val price = maxPrice - (priceRange * i / gridLineCount)
        val priceText = formatPriceForAxis(price)
        val textLayoutResult = textMeasurer.measure(
            text = priceText,
            style = TextStyle(fontSize = 9.sp, color = textColor)
        )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(chartWidth + 8f, y - textLayoutResult.size.height / 2)
        )
    }
}

private fun DrawScope.drawGradientFill(
    points: List<Offset>,
    chartHeight: Float,
    lineColor: Color
) {
    if (points.size < 2) return
    val fillPath = Path().apply {
        moveTo(points.first().x, chartHeight)
        lineTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
            val current = points[i]
            val next = points[i + 1]
            val controlPoint1X = current.x + (next.x - current.x) / 3f
            val controlPoint2X = current.x + 2 * (next.x - current.x) / 3f
            cubicTo(
                controlPoint1X, current.y,
                controlPoint2X, next.y,
                next.x, next.y
            )
        }
        lineTo(points.last().x, chartHeight)
        close()
    }
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                lineColor.copy(alpha = 0.3f),
                lineColor.copy(alpha = 0.1f),
                lineColor.copy(alpha = 0.0f)
            ),
            startY = 0f,
            endY = chartHeight
        )
    )
}

private fun DrawScope.drawSmoothLine(
    points: List<Offset>,
    lineColor: Color
) {
    if (points.size < 2) return
    val linePath = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
            val current = points[i]
            val next = points[i + 1]
            val controlPoint1X = current.x + (next.x - current.x) / 3f
            val controlPoint2X = current.x + 2 * (next.x - current.x) / 3f
            cubicTo(
                controlPoint1X, current.y,
                controlPoint2X, next.y,
                next.x, next.y
            )
        }
    }
    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(
            width = 2.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawCurrentPriceLine(
    chartWidth: Float,
    chartHeight: Float,
    currentPrice: Double,
    minPrice: Double,
    priceRange: Double,
    lineColor: Color,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val normalizedPrice = if (priceRange > 0) (currentPrice - minPrice) / priceRange else 0.5
    val y = chartHeight * (1 - normalizedPrice.toFloat())
    drawLine(
        color = lineColor,
        start = Offset(0f, y),
        end = Offset(chartWidth, y),
        strokeWidth = 1.5f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
    )
    val priceText = formatPriceForAxis(currentPrice)
    val textLayoutResult = textMeasurer.measure(
        text = priceText,
        style = TextStyle(fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
    )
    val labelWidth = textLayoutResult.size.width + 16f
    val labelHeight = textLayoutResult.size.height + 8f
    drawRoundRect(
        color = lineColor,
        topLeft = Offset(chartWidth + 4f, y - labelHeight / 2),
        size = Size(labelWidth, labelHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(chartWidth + 12f, y - textLayoutResult.size.height / 2)
    )
}

private fun DrawScope.drawCrosshair(
    position: Offset,
    chartWidth: Float,
    chartHeight: Float,
    crosshairColor: Color
) {
    drawLine(
        color = crosshairColor,
        start = Offset(position.x, 0f),
        end = Offset(position.x, chartHeight),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
    )
    drawLine(
        color = crosshairColor,
        start = Offset(0f, position.y),
        end = Offset(chartWidth, position.y),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
    )
    drawCircle(
        color = crosshairColor,
        radius = 6f,
        center = position
    )
    drawCircle(
        color = Color.White,
        radius = 3f,
        center = position
    )
}

private fun formatPriceForAxis(price: Double): String {
    return when {
        price >= 10000 -> "${(price / 1000).toInt()}K"
        price >= 1000 ->formatCoinPrice(price)
        price >= 100 -> formatCoinPrice(price)
        price >= 1 -> formatCoinPrice(price)
        price >= 0.01 -> formatCoinPrice(price)
        else -> formatCoinPrice(price)
    }
}

