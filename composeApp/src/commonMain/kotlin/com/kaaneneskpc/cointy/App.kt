package com.kaaneneskpc.cointy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kaaneneskpc.cointy.biometric.BiometricScreen
import com.kaaneneskpc.cointy.coins.presentation.CoinListScreen
import com.kaaneneskpc.cointy.core.navigation.Biometric
import com.kaaneneskpc.cointy.core.navigation.Buy
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kaaneneskpc.cointy.core.navigation.Coins
import com.kaaneneskpc.cointy.core.navigation.Portfolio
import com.kaaneneskpc.cointy.core.navigation.Sell
import com.kaaneneskpc.cointy.core.navigation.TransactionHistory
import com.kaaneneskpc.cointy.portfolio.presentation.PortfolioScreen
import com.kaaneneskpc.cointy.theme.CointyTheme
import com.kaaneneskpc.cointy.trade.presentation.buy.BuyScreen
import com.kaaneneskpc.cointy.trade.presentation.sell.SellScreen
import com.kaaneneskpc.cointy.transaction.presentation.TransactionHistoryScreen

@Composable
@Preview
fun App() {
    val navController: NavHostController = rememberNavController()
    CointyTheme {
        NavHost(
            navController = navController,
            startDestination = Portfolio,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<Biometric> {
                BiometricScreen {
                    navController.navigate(Portfolio)
                }
            }
            composable<Portfolio> {
                PortfolioScreen(
                    onCoinItemClicked = { coinId ->
                        navController.navigate(Sell(coinId))
                    },
                    onDiscoverCoinsClicked = {
                        navController.navigate(Coins)
                    },
                    onTransactionHistoryClicked = {
                        navController.navigate(TransactionHistory)
                    }
                )
            }
            
            composable<TransactionHistory> {
                TransactionHistoryScreen(
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }

            composable<Coins> {
                CoinListScreen { coinId ->
                    navController.navigate(Buy(coinId))
                }
            }

            composable<Buy> { navBackStackEntry ->
                val coinId: String = navBackStackEntry.toRoute<Buy>().coinId
                BuyScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    }
                )
            }
            composable<Sell> { navBackStackEntry ->
                val coinId: String = navBackStackEntry.toRoute<Sell>().coinId
                SellScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    }
                )
            }

        }
    }
}