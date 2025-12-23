package com.kaaneneskpc.cointy.risk.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaaneneskpc.cointy.risk.domain.model.RiskLevel

@Composable
fun RiskScoreGauge(
    modifier: Modifier = Modifier,
    riskScore: Int,
    riskLevel: RiskLevel
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(riskScore) {
        animatedProgress.animateTo(
            targetValue = riskScore / 100f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }
    val gaugeColor = getGaugeColor(riskLevel)
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val strokeWidth = 20.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)
            drawArc(
                color = backgroundColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = gaugeColor,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress.value,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = riskScore.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                ),
                color = gaugeColor
            )
            Text(
                text = getRiskLevelText(riskLevel),
                style = MaterialTheme.typography.titleMedium,
                color = gaugeColor
            )
        }
    }
}

private fun getGaugeColor(riskLevel: RiskLevel): Color {
    return when (riskLevel) {
        RiskLevel.LOW -> Color(0xFF10B981)
        RiskLevel.MODERATE -> Color(0xFFF59E0B)
        RiskLevel.HIGH -> Color(0xFFF97316)
        RiskLevel.CRITICAL -> Color(0xFFEF4444)
    }
}

private fun getRiskLevelText(riskLevel: RiskLevel): String {
    return when (riskLevel) {
        RiskLevel.LOW -> "Low Risk"
        RiskLevel.MODERATE -> "Moderate"
        RiskLevel.HIGH -> "High Risk"
        RiskLevel.CRITICAL -> "Critical"
    }
}
