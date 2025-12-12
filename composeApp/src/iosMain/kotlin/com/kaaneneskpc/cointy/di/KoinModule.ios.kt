package com.kaaneneskpc.cointy.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformModule = module {

    //Core
    single <HttpClientEngine>{ Darwin.create() }

}