package com.kaaneneskpc.cointy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaaneneskpc.cointy.coins.presentation.CoinListScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kaaneneskpc.cointy.core.navigation.Coins
import com.kaaneneskpc.cointy.theme.CointyTheme

@Composable
@Preview
fun App() {
    val navController: NavHostController = rememberNavController()
    CointyTheme {
        NavHost(
            navController = navController,
            startDestination = Coins,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<Coins> {
                CoinListScreen {
                    navController.navigate("coinId")
                }
            }
        }
    }
}