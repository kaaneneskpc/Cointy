package com.kaaneneskpc.cointy.core.biometric

import androidx.compose.runtime.Composable
import com.kaaneneskpc.cointy.biometric.IosBiometricAuthenticator

object IosPlatformContext : PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = IosPlatformContext

actual fun getBiometricAuthenticator(context: PlatformContext): BiometricAuthenticator =
    IosBiometricAuthenticator()