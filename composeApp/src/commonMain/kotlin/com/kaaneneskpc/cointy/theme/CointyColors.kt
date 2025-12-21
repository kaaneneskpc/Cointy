package com.kaaneneskpc.cointy.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CoinRoutineColorsPalette(
    val profitGreen: Color = Color.Unspecified,
    val lossRed: Color = Color.Unspecified,
)

val ProfitGreenColor = Color(color = 0xFF32de84)
val LossRedColor = Color(color = 0xFFD2122E)

val DarkProfitGreenColor = Color(color = 0xFF32de84)
val DarkLossRedColor = Color(color = 0xFFD2122E)

val LightCoinRoutineColorsPalette = CoinRoutineColorsPalette(
    profitGreen = ProfitGreenColor,
    lossRed = LossRedColor,
)

val DarkCoinRoutineColorsPalette = CoinRoutineColorsPalette(
    profitGreen = DarkProfitGreenColor,
    lossRed = DarkLossRedColor,
)

val LocalCoinRoutineColorsPalette = compositionLocalOf { CoinRoutineColorsPalette() }

object CointyColors {
    val Primary = Color(0xFF485F84)
    val Profit = ProfitGreenColor
    val Loss = LossRedColor
}