package com.kaaneneskpc.cointy.biometric

import com.kaaneneskpc.cointy.core.biometric.BiometricAuthenticator
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IosBiometricAuthenticator : BiometricAuthenticator {

    override suspend fun authenticate(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val context = LAContext().also {
                it.evaluatePolicy(
                    LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                    localizedReason = "Authenticate using biometrics",
                    reply = { success, error ->
                        if (success) {
                            if (continuation.isActive) {
                                continuation.resume(true)
                            }
                        } else {
                            val message = error?.localizedDescription ?: "Authentication failed"
                            if (continuation.isActive) {
                                continuation.resumeWithException(Exception(message))
                            }
                        }
                    }
                )
            }
        }
    }
}