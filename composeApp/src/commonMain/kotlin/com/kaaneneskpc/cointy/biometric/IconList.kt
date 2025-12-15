package com.kaaneneskpc.cointy.biometric

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cointy.composeapp.generated.resources.Res
import cointy.composeapp.generated.resources.ic_face_id
import cointy.composeapp.generated.resources.ic_fingerprint
import com.kaaneneskpc.cointy.Platform
import com.kaaneneskpc.cointy.platform
import org.jetbrains.compose.resources.vectorResource

val BiometricIcon: ImageVector
    @Composable
    get() = when (platform) {
        is Platform.Android -> vectorResource(Res.drawable.ic_fingerprint)
        is Platform.Ios -> vectorResource(Res.drawable.ic_face_id)
    }