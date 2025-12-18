package com.kaaneneskpc.cointy.analytics.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.cointy.analytics.domain.model.PortfolioHistoryPoint

@Composable
fun PortfolioHistoryChart(
    modifier: Modifier = Modifier,
    historyPoints: List<PortfolioHistoryPoint>,
    profitColor: Color,
    lossColor: Color
) {
    if (historyPoints.size < 2) return
    val values = historyPoints.map { it.totalValue }
    val isPositive = values.last() >= values.first()
    val lineColor = if (isPositive) profitColor else lossColor
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(historyPoints) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
    }
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val padding = 16.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2
        val maxValue = values.maxOrNull() ?: return@Canvas
        val minValue = values.minOrNull() ?: return@Canvas
        val range = (maxValue - minValue).coerceAtLeast(1.0)
        val gridColor = Color.Gray.copy(alpha = 0.1f)
        val gridLineCount = 4
        for (i in 0..gridLineCount) {
            val y = padding + (chartHeight / gridLineCount) * i
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(size.width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        val points = values.mapIndexed { index, value ->
            val x = padding + (index * chartWidth / (values.size - 1).coerceAtLeast(1))
            val normalizedValue = ((value - minValue) / range).coerceIn(0.0, 1.0)
            val y = padding + chartHeight * (1 - normalizedValue).toFloat()
            Offset(x, y)
        }
        val visiblePointCount = (points.size * animationProgress.value).toInt().coerceAtLeast(2)
        val visiblePoints = points.take(visiblePointCount)
        val linePath = Path()
        val fillPath = Path()
        if (visiblePoints.isNotEmpty()) {
            linePath.moveTo(visiblePoints[0].x, visiblePoints[0].y)
            fillPath.moveTo(visiblePoints[0].x, padding + chartHeight)
            fillPath.lineTo(visiblePoints[0].x, visiblePoints[0].y)
            for (i in 0 until visiblePoints.size - 1) {
                val current = visiblePoints[i]
                val next = visiblePoints[i + 1]
                val controlX1 = current.x + (next.x - current.x) / 3f
                val controlY1 = current.y
                val controlX2 = current.x + 2 * (next.x - current.x) / 3f
                val controlY2 = next.y
                linePath.cubicTo(controlX1, controlY1, controlX2, controlY2, next.x, next.y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, next.x, next.y)
            }
            val lastPoint = visiblePoints.last()
            fillPath.lineTo(lastPoint.x, padding + chartHeight)
            fillPath.close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.3f),
                    lineColor.copy(alpha = 0.0f)
                ),
                startY = padding,
                endY = padding + chartHeight
            )
        )
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        if (visiblePoints.isNotEmpty()) {
            val lastVisiblePoint = visiblePoints.last()
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = lastVisiblePoint
            )
            drawCircle(
                color = lineColor.copy(alpha = 0.3f),
                radius = 12.dp.toPx(),
                center = lastVisiblePoint
            )
        }
    }
}

