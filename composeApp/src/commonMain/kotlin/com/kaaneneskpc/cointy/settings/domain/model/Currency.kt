package com.kaaneneskpc.cointy.settings.domain.model

enum class Currency(val code: String, val symbol: String, val displayName: String) {
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro"),
    TRY("TRY", "₺", "Turkish Lira"),
    GBP("GBP", "£", "British Pound")
}



