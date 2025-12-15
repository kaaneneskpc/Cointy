package com.kaaneneskpc.cointy.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cointy.composeapp.generated.resources.Res
import cointy.composeapp.generated.resources.Ubuntu_Bold
import cointy.composeapp.generated.resources.Ubuntu_Light
import cointy.composeapp.generated.resources.Ubuntu_Medium
import cointy.composeapp.generated.resources.Ubuntu_Regular
import org.jetbrains.compose.resources.Font


@Composable
fun UbuntuFontFamily() = FontFamily(
    Font(Res.font.Ubuntu_Light, weight = FontWeight.Light),
    Font(Res.font.Ubuntu_Regular, weight = FontWeight.Normal),
    Font(Res.font.Ubuntu_Medium, weight = FontWeight.Medium),
    Font(Res.font.Ubuntu_Bold, weight = FontWeight.Bold),
)