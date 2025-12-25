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
import com.kaaneneskpc.cointy.auth.domain.AuthRepository
import com.kaaneneskpc.cointy.auth.presentation.forgot_password.ForgotPasswordScreen
import com.kaaneneskpc.cointy.auth.presentation.login.LoginScreen
import com.kaaneneskpc.cointy.auth.presentation.register.RegisterScreen
import com.kaaneneskpc.cointy.biometric.BiometricScreen
import com.kaaneneskpc.cointy.coins.presentation.CoinListScreen
import com.kaaneneskpc.cointy.core.localization.ProvideStringResources
import com.kaaneneskpc.cointy.core.navigation.Analytics
import com.kaaneneskpc.cointy.core.navigation.Biometric
import com.kaaneneskpc.cointy.core.navigation.Buy
import com.kaaneneskpc.cointy.core.navigation.CreateAlert
import com.kaaneneskpc.cointy.core.navigation.ForgotPassword
import com.kaaneneskpc.cointy.core.navigation.Login
import com.kaaneneskpc.cointy.core.navigation.PriceAlerts
import com.kaaneneskpc.cointy.core.navigation.Register
import com.kaaneneskpc.cointy.core.navigation.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kaaneneskpc.cointy.core.navigation.Coins
import com.kaaneneskpc.cointy.core.navigation.Portfolio
import com.kaaneneskpc.cointy.core.navigation.Sell
import com.kaaneneskpc.cointy.core.navigation.TransactionHistory
import com.kaaneneskpc.cointy.core.navigation.Export
import com.kaaneneskpc.cointy.core.navigation.RiskAnalysis
import com.kaaneneskpc.cointy.export.presentation.ExportScreen
import com.kaaneneskpc.cointy.onboarding.presentation.OnboardingScreen
import com.kaaneneskpc.cointy.core.navigation.Onboarding
import com.kaaneneskpc.cointy.portfolio.presentation.PortfolioScreen
import com.kaaneneskpc.cointy.settings.presentation.SettingsScreen
import com.kaaneneskpc.cointy.settings.presentation.SettingsViewModel
import com.kaaneneskpc.cointy.theme.CointyTheme
import com.kaaneneskpc.cointy.trade.presentation.buy.BuyScreen
import com.kaaneneskpc.cointy.trade.presentation.sell.SellScreen
import com.kaaneneskpc.cointy.transaction.presentation.TransactionHistoryScreen
import com.kaaneneskpc.cointy.risk.presentation.RiskAnalysisScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val navController: NavHostController = rememberNavController()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val authRepository: AuthRepository = koinInject()

    var startDestination by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(settingsState.isDataLoaded, settingsState.isOnboardingCompleted) {
        if (startDestination == null && settingsState.isDataLoaded) {
            val isLoggedIn = authRepository.isUserLoggedIn()
            startDestination = when {
                !settingsState.isOnboardingCompleted -> Onboarding
                !isLoggedIn -> Login
                else -> Portfolio
            }
        }
    }

    CointyTheme(themeMode = settingsState.themeMode) {
        ProvideStringResources(language = settingsState.language) {
            if (startDestination != null) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination!!,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<Login> {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Portfolio) {
                                    popUpTo(Login) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(Register)
                            },
                            onNavigateToForgotPassword = {
                                navController.navigate(ForgotPassword)
                            }
                        )
                    }
                    composable<Register> {
                        RegisterScreen(
                            onRegistrationSuccess = {
                                navController.navigate(Onboarding) {
                                    popUpTo(Login) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable<ForgotPassword> {
                        ForgotPasswordScreen(
                            onBackToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable<Onboarding> {
                        OnboardingScreen {
                            navController.navigate(Login) {
                                popUpTo(Onboarding) { inclusive = true }
                            }
                        }
                    }
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
                            },
                            onExportClicked = {
                                navController.navigate(Export)
                            }
                        )
                    }
                    composable<Settings> {
                        SettingsScreen(
                            onBackClicked = {
                                navController.popBackStack()
                            },
                            onLogout = {
                                navController.navigate(Login) {
                                    popUpTo(0) { inclusive = true }
                                }
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
                            },
                            onRiskAnalysisClicked = {
                                navController.navigate(RiskAnalysis)
                            }
                        )
                    }
                    composable<RiskAnalysis> {
                        RiskAnalysisScreen(
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
                            },
                            onBackClicked = {
                                navController.popBackStack()
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
                            },
                            onBackClicked = {
                                navController.popBackStack()
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
                            },
                            onBackClicked = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable<Export> {
                        ExportScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
