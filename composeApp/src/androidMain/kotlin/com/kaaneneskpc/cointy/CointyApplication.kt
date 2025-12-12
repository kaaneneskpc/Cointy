package com.kaaneneskpc.cointy

import android.app.Application
import com.kaaneneskpc.cointy.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class CointyApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CointyApplication)
        }
    }

}