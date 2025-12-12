package com.kaaneneskpc.cointy

import androidx.compose.ui.window.ComposeUIViewController
import com.kaaneneskpc.cointy.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }