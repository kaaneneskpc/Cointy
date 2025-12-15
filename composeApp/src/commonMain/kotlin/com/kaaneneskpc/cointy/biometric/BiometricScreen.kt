package com.kaaneneskpc.cointy.biometric

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kaaneneskpc.cointy.core.biometric.BiometricAuthNotAvailable
import com.kaaneneskpc.cointy.core.biometric.getBiometricAuthenticator
import com.kaaneneskpc.cointy.core.biometric.getPlatformContext
import com.kaaneneskpc.cointy.theme.LocalCoinRoutineColorsPalette
import com.kaaneneskpc.cointy.theme.UbuntuFontFamily
import kotlinx.coroutines.launch

@Composable
fun BiometricScreen(
    onSuccess: () -> Unit,
) {
    val platformContext = getPlatformContext()
    val biometricAuthenticator = remember { getBiometricAuthenticator(platformContext) }
    val coroutineScope = rememberCoroutineScope()
    var authError by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500
            ),
            repeatMode = RepeatMode.Reverse
        ),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Cointy",
                color = MaterialTheme.colorScheme.onBackground,
                style = TextStyle(
                    fontFamily = UbuntuFontFamily()
                ),
                fontSize = MaterialTheme.typography.displayMedium.fontSize
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Powered by Compose Multiplatform",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
            )
            Spacer(modifier = Modifier.height(64.dp))
            Icon(
                imageVector = BiometricIcon,
                contentDescription = "Biometric Icon",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(64.dp).scale(scale)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val authenticated = biometricAuthenticator.authenticate()
                            authError = null
                            if (authenticated) {
                                onSuccess()
                            }
                        } catch (e: Exception) {
                            authError = e.message
                            if (e.message == BiometricAuthNotAvailable.BIOAUTH_NOT_AVAILABLE.toString()) {
                                authError = "Biometric is not available on your device!"
                            }
                        }
                    }
                }
            ) {
                Text(
                    text = "Login"
                )
            }
            authError?.let {
                Text(
                    text = it,
                    color = LocalCoinRoutineColorsPalette.current.lossRed,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                )
            }
        }
    }
}