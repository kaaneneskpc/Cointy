package com.kaaneneskpc.cointy.di

import androidx.room.RoomDatabase
import com.kaaneneskpc.cointy.coins.data.remote.impl.CoinRemoteDataSourceImpl
import com.kaaneneskpc.cointy.coins.domain.GetCoinPriceHistoryUseCase
import com.kaaneneskpc.cointy.coins.domain.GetCoinsListUseCase
import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.coins.presentation.CoinListViewModel
import com.kaaneneskpc.cointy.core.database.portfolio.PortfolioDatabase
import com.kaaneneskpc.cointy.core.database.portfolio.getPortfolioDatabase
import com.kaaneneskpc.cointy.core.network.HttpClientFactory
import com.kaaneneskpc.cointy.portfolio.data.PortfolioRepositoryImpl
import com.kaaneneskpc.cointy.portfolio.domain.PortfolioRepository
import com.kaaneneskpc.cointy.portfolio.presentation.PortfolioViewModel
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

    //CoinList
    viewModel { CoinListViewModel(get(), get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)
    singleOf(::CoinRemoteDataSourceImpl).bind<CoinRemoteDataSource>()
}