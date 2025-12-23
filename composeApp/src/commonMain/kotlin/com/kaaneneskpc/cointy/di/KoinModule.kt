package com.kaaneneskpc.cointy.di

import androidx.room.RoomDatabase
import com.kaaneneskpc.cointy.alert.data.PriceAlertRepositoryImpl
import com.kaaneneskpc.cointy.alert.data.VolatilityNotificationTracker
import com.kaaneneskpc.cointy.alert.domain.BackgroundCheckPriceAlertsUseCase
import com.kaaneneskpc.cointy.alert.domain.CheckPriceAlertsUseCase
import com.kaaneneskpc.cointy.alert.domain.CheckVolatilityAlertsUseCase
import com.kaaneneskpc.cointy.alert.domain.CreatePriceAlertUseCase
import com.kaaneneskpc.cointy.alert.domain.DeletePriceAlertUseCase
import com.kaaneneskpc.cointy.alert.domain.GetPriceAlertsUseCase
import com.kaaneneskpc.cointy.alert.domain.PriceAlertRepository
import com.kaaneneskpc.cointy.alert.domain.TogglePriceAlertUseCase
import com.kaaneneskpc.cointy.alert.presentation.PriceAlertViewModel
import com.kaaneneskpc.cointy.analytics.data.AnalyticsRepositoryImpl
import com.kaaneneskpc.cointy.analytics.domain.AnalyticsRepository
import com.kaaneneskpc.cointy.analytics.domain.GetPortfolioAnalyticsUseCase
import com.kaaneneskpc.cointy.analytics.presentation.AnalyticsViewModel
import com.kaaneneskpc.cointy.risk.data.RiskAnalysisRepositoryImpl
import com.kaaneneskpc.cointy.risk.domain.GetPortfolioRiskAnalysisUseCase
import com.kaaneneskpc.cointy.risk.domain.RiskAnalysisRepository
import com.kaaneneskpc.cointy.risk.presentation.RiskAnalysisViewModel
import com.kaaneneskpc.cointy.coins.data.remote.impl.CoinRemoteDataSourceImpl
import com.kaaneneskpc.cointy.coins.domain.GetCoinDetailsUseCase
import com.kaaneneskpc.cointy.coins.domain.GetCoinPriceHistoryUseCase
import com.kaaneneskpc.cointy.coins.domain.GetCoinsListUseCase
import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.coins.presentation.CoinListViewModel
import com.kaaneneskpc.cointy.core.database.portfolio.PortfolioDatabase
import com.kaaneneskpc.cointy.core.database.portfolio.getPortfolioDatabase
import com.kaaneneskpc.cointy.core.network.HttpClientFactory
import com.kaaneneskpc.cointy.export.data.ExportRepositoryImpl
import com.kaaneneskpc.cointy.export.domain.ExportPortfolioDataUseCase
import com.kaaneneskpc.cointy.export.domain.ExportRepository
import com.kaaneneskpc.cointy.export.domain.GetExportDataUseCase
import com.kaaneneskpc.cointy.export.presentation.ExportViewModel
import com.kaaneneskpc.cointy.portfolio.data.PortfolioRepositoryImpl
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioRepository
import com.kaaneneskpc.cointy.portfolio.presentation.PortfolioViewModel
import com.kaaneneskpc.cointy.core.datastore.createDataStore
import com.kaaneneskpc.cointy.settings.data.DataStoreSettingsDataSource
import com.kaaneneskpc.cointy.settings.data.SettingsDataSource
import com.kaaneneskpc.cointy.settings.data.SettingsRepositoryImpl
import com.kaaneneskpc.cointy.settings.domain.SettingsRepository
import com.kaaneneskpc.cointy.settings.presentation.SettingsViewModel
import com.kaaneneskpc.cointy.trade.domain.BuyCoinUseCase
import com.kaaneneskpc.cointy.trade.domain.SellCoinUseCase
import com.kaaneneskpc.cointy.trade.presentation.buy.BuyViewModel
import com.kaaneneskpc.cointy.trade.presentation.sell.SellViewModel
import com.kaaneneskpc.cointy.onboarding.presentation.OnboardingViewModel
import com.kaaneneskpc.cointy.transaction.data.TransactionRepositoryImpl
import com.kaaneneskpc.cointy.transaction.domain.GetTransactionHistoryUseCase
import com.kaaneneskpc.cointy.transaction.domain.TransactionRepository
import com.kaaneneskpc.cointy.transaction.presentation.TransactionHistoryViewModel
import com.kaaneneskpc.cointy.widget.domain.GetWidgetDataUseCase
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule,
            platformModule
        )
    }

expect val platformModule: Module

val sharedModule = module {
    //Core
    single<HttpClient> { HttpClientFactory.create(get()) }

    //Portfolio
    single { getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>()) }
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().userBalanceDao() }
    viewModel { PortfolioViewModel(get()) }
    
    //Transaction
    single { get<PortfolioDatabase>().transactionDao() }
    singleOf(::TransactionRepositoryImpl).bind<TransactionRepository>()
    singleOf(::GetTransactionHistoryUseCase)
    viewModel { TransactionHistoryViewModel(get()) }

    //Analytics
    singleOf(::AnalyticsRepositoryImpl).bind<AnalyticsRepository>()
    singleOf(::GetPortfolioAnalyticsUseCase)
    viewModel { AnalyticsViewModel(get()) }

    //RiskAnalysis
    singleOf(::RiskAnalysisRepositoryImpl).bind<RiskAnalysisRepository>()
    singleOf(::GetPortfolioRiskAnalysisUseCase)
    viewModel { RiskAnalysisViewModel(get()) }

    //CoinList
    viewModel { CoinListViewModel(get(), get(), get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)
    singleOf(::GetCoinDetailsUseCase)
    singleOf(::CoinRemoteDataSourceImpl).bind<CoinRemoteDataSource>()

    //Trade
    singleOf(::BuyCoinUseCase)
    singleOf(::SellCoinUseCase)
    viewModel { (coinId: String) -> BuyViewModel(get(), get(), get(), coinId) }
    viewModel { (coinId: String) -> SellViewModel(get(), get(), get(), coinId) }

    //PriceAlert
    single { get<PortfolioDatabase>().priceAlertDao() }
    singleOf(::PriceAlertRepositoryImpl).bind<PriceAlertRepository>()
    singleOf(::GetPriceAlertsUseCase)
    singleOf(::CreatePriceAlertUseCase)
    singleOf(::DeletePriceAlertUseCase)
    singleOf(::TogglePriceAlertUseCase)
    singleOf(::CheckPriceAlertsUseCase)
    single { VolatilityNotificationTracker() }
    singleOf(::CheckVolatilityAlertsUseCase)
    singleOf(::BackgroundCheckPriceAlertsUseCase)
    viewModel { PriceAlertViewModel(get(), get(), get(), get()) }
    //Settings
    single { createDataStore() }
    single<SettingsDataSource> { DataStoreSettingsDataSource(get()) }
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    viewModel { SettingsViewModel(get()) }
    //Export
    single<ExportRepository> { ExportRepositoryImpl(get(), get(), get()) }
    singleOf(::GetExportDataUseCase)
    singleOf(::ExportPortfolioDataUseCase)
    viewModel { ExportViewModel(get(), get()) }

    //Onboarding
    viewModel { OnboardingViewModel(get()) }

    //Widget
    singleOf(::GetWidgetDataUseCase)
}
