package com.kaaneneskpc.cointy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform