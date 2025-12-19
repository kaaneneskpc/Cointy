package com.kaaneneskpc.cointy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kaaneneskpc.cointy.alert.presentation.CreateAlertScreen
import com.kaaneneskpc.cointy.alert.presentation.PriceAlertScreen
import com.kaaneneskpc.cointy.analytics.presentation.AnalyticsScreen
import com.kaaneneskpc.cointy.biometric.BiometricScreen
import com.kaaneneskpc.cointy.coins.presentation.CoinListScreen
import com.kaaneneskpc.cointy.core.localization.ProvideStringResources
import com.kaaneneskpc.cointy.core.navigation.Analytics
import com.kaaneneskpc.cointy.core.navigation.Biometric
import com.kaaneneskpc.cointy.core.navigation.Buy
import com.kaaneneskpc.cointy.core.navigation.CreateAlert
import com.kaaneneskpc.cointy.core.navigation.PriceAlerts
import com.kaaneneskpc.cointy.core.navigation.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kaaneneskpc.cointy.core.navigation.Coins
import com.kaaneneskpc.cointy.core.navigation.Portfolio
import com.kaaneneskpc.cointy.core.navigation.Sell
import com.kaaneneskpc.cointy.core.navigation.TransactionHistory
import com.kaaneneskpc.cointy.portfolio.presentation.PortfolioScreen
import com.kaaneneskpc.cointy.settings.presentation.SettingsScreen
import com.kaaneneskpc.cointy.settings.presentation.SettingsViewModel
import com.kaaneneskpc.cointy.theme.CointyTheme
import com.kaaneneskpc.cointy.trade.presentation.buy.BuyScreen
import com.kaaneneskpc.cointy.trade.presentation.sell.SellScreen
import com.kaaneneskpc.cointy.transaction.presentation.TransactionHistoryScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val navController: NavHostController = rememberNavController()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    CointyTheme(themeMode = settingsState.themeMode) {
        ProvideStringResources(language = settingsState.language) {
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
                        },
                        onAnalyticsClicked = {
                            navController.navigate(Analytics)
                        },
                        onPriceAlertsClicked = {
                            navController.navigate(PriceAlerts)
                        },
                        onSettingsClicked = {
                            navController.navigate(Settings)
                        }
                    )
                }
                composable<Settings> {
                    SettingsScreen(
                        onBackClicked = {
                            navController.popBackStack()
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
                composable<Analytics> {
                    AnalyticsScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }
                composable<PriceAlerts> {
                    PriceAlertScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onAddAlertClicked = {
                            navController.navigate(Coins)
                        }
                    )
                }
                composable<CreateAlert> { navBackStackEntry ->
                    val coinId: String = navBackStackEntry.toRoute<CreateAlert>().coinId
                    CreateAlertScreen(
                        coinId = coinId,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onAlertCreated = {
                            navController.navigate(PriceAlerts) {
                                popUpTo(PriceAlerts) { inclusive = true }
                            }
                        }
                    )
                }
                composable<Coins> {
                    CoinListScreen(
                        onCoinClicked = { coinId ->
                            navController.navigate(Buy(coinId))
                        },
                        onCreateAlertClicked = { coinId ->
                            navController.navigate(CreateAlert(coinId))
                        }
                    )
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
}
