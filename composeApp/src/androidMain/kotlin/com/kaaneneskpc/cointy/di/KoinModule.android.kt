package com.kaaneneskpc.cointy.di
import androidx.room.RoomDatabase
import com.kaaneneskpc.cointy.core.background.BackgroundPriceChecker
import com.kaaneneskpc.cointy.core.background.createBackgroundPriceChecker
import com.kaaneneskpc.cointy.core.database.getPortfolioDatabaseBuilder
import com.kaaneneskpc.cointy.core.database.portfolio.PortfolioDatabase
import com.kaaneneskpc.cointy.core.export.AndroidFileExporter
import com.kaaneneskpc.cointy.core.export.FileExporter
import com.kaaneneskpc.cointy.core.notification.AndroidNotificationService
import com.kaaneneskpc.cointy.core.notification.NotificationManager
import com.kaaneneskpc.cointy.core.notification.NotificationService
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    //Core
    single <HttpClientEngine>{ Android.create() }
    //Portfolio
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
    //Notification
    singleOf(::NotificationManager)
    singleOf(::AndroidNotificationService).bind<NotificationService>()
    //Export
    singleOf(::AndroidFileExporter).bind<FileExporter>()
    //Background
    single<BackgroundPriceChecker> { createBackgroundPriceChecker(get()) }
}