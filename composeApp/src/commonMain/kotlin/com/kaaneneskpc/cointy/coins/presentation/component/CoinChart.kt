package com.kaaneneskpc.cointy.coins.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CoinChart(
    modifier: Modifier = Modifier,
    nodes: List<Double>,
    profitColor: Color,
    lossColor: Color,
) {
    if (nodes.isEmpty()) return

    val max = nodes.maxOrNull() ?: return
    val min = nodes.minOrNull() ?: return
    val range = max - min
    val isPositive = nodes.last() > nodes.first()
    val lineColor = if (isPositive) profitColor else lossColor

    val fillGradientStart = if (isPositive) {
        profitColor.copy(alpha = 0.3f)
    } else {
        lossColor.copy(alpha = 0.3f)
    }
    val fillGradientEnd = if (isPositive) {
        profitColor.copy(alpha = 0.0f)
    } else {
        lossColor.copy(alpha = 0.0f)
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val padding = 8.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2
        val startX = padding
        val startY = padding

        val gridColor = Color.Gray.copy(alpha = 0.1f)
        val gridLineCount = 4
        for (i in 0..gridLineCount) {
            val y = startY + (chartHeight / gridLineCount) * i
            drawLine(
                color = gridColor,
                start = Offset(startX, y),
                end = Offset(startX + chartWidth, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val points = nodes.mapIndexed { index, value ->
            val x = startX + (index * chartWidth / (nodes.size - 1).coerceAtLeast(1))
            val normalizedValue = if (range > 0) (value - min) / range else 0.5f
            val y = startY + chartHeight * (1 - normalizedValue.toFloat())
            Offset(x, y)
        }

        val smoothPath = Path()
        val fillPath = Path()
        
        if (points.isNotEmpty()) {
            smoothPath.moveTo(points[0].x, points[0].y)
            fillPath.moveTo(points[0].x, startY + chartHeight)
            fillPath.lineTo(points[0].x, points[0].y)
            
            for (i in 0 until points.size - 1) {
                val current = points[i]
                val next = points[i + 1]
                
                val controlPoint1X = current.x + (next.x - current.x) / 3f
                val controlPoint1Y = current.y
                val controlPoint2X = current.x + 2 * (next.x - current.x) / 3f
                val controlPoint2Y = next.y
                
                smoothPath.cubicTo(
                    controlPoint1X, controlPoint1Y,
                    controlPoint2X, controlPoint2Y,
                    next.x, next.y
                )
                
                fillPath.cubicTo(
                    controlPoint1X, controlPoint1Y,
                    controlPoint2X, controlPoint2Y,
                    next.x, next.y
                )
            }

            val lastPoint = points.last()
            fillPath.lineTo(lastPoint.x, startY + chartHeight)
            fillPath.close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillGradientStart, fillGradientEnd),
                startY = startY,
                endY = startY + chartHeight
            )
        )

        drawPath(
            path = smoothPath,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )

        points.forEach { point ->
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = point
            )
            drawCircle(
                color = lineColor.copy(alpha = 0.2f),
                radius = 6.dp.toPx(),
                center = point
            )
        }
    }
}