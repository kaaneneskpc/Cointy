package com.kaaneneskpc.cointy.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.kaaneneskpc.cointy.settings.domain.model.Language

data class StringResources(
    val settings: String,
    val profile: String,
    val editProfile: String,
    val firstName: String,
    val lastName: String,
    val save: String,
    val cancel: String,
    val appearance: String,
    val systemDefault: String,
    val followSystemTheme: String,
    val lightMode: String,
    val alwaysLightTheme: String,
    val darkMode: String,
    val alwaysDarkTheme: String,
    val language: String,
    val selectLanguage: String,
    val currency: String,
    val selectCurrency: String,
    val notifications: String,
    val enableNotifications: String,
    val priceAlerts: String,
    val getPriceAlertNotifications: String,
    val about: String,
    val version: String,
    val termsOfService: String,
    val privacyPolicy: String,
    val rateApp: String,
    val portfolio: String,
    val totalValue: String,
    val cashBalance: String,
    val discoverCoins: String,
    val history: String,
    val analytics: String,
    val alerts: String,
    val buy: String,
    val sell: String,
    val availableBalance: String,
    val amountOwned: String,
    val transactionHistory: String,
    val noTransactions: String,
    val back: String,
    val general: String,
    val preferences: String,
    val tapToEdit: String,
    val profileUpdated: String,
    val enterFirstName: String,
    val enterLastName: String
)

val EnglishStrings = StringResources(
    settings = "Settings",
    profile = "Profile",
    editProfile = "Edit Profile",
    firstName = "First Name",
    lastName = "Last Name",
    save = "Save",
    cancel = "Cancel",
    appearance = "Appearance",
    systemDefault = "System Default",
    followSystemTheme = "Follow system theme",
    lightMode = "Light Mode",
    alwaysLightTheme = "Always use light theme",
    darkMode = "Dark Mode",
    alwaysDarkTheme = "Always use dark theme",
    language = "Language",
    selectLanguage = "Select language",
    currency = "Currency",
    selectCurrency = "Select currency",
    notifications = "Notifications",
    enableNotifications = "Enable notifications",
    priceAlerts = "Price Alerts",
    getPriceAlertNotifications = "Get notified when price targets are reached",
    about = "About",
    version = "Version",
    termsOfService = "Terms of Service",
    privacyPolicy = "Privacy Policy",
    rateApp = "Rate App",
    portfolio = "Portfolio",
    totalValue = "Total Value",
    cashBalance = "Cash Balance",
    discoverCoins = "Discover Coins",
    history = "History",
    analytics = "Analytics",
    alerts = "Alerts",
    buy = "Buy",
    sell = "Sell",
    availableBalance = "Available Balance",
    amountOwned = "Amount Owned",
    transactionHistory = "Transaction History",
    noTransactions = "No transactions yet",
    back = "Back",
    general = "General",
    preferences = "Preferences",
    tapToEdit = "Tap to edit",
    profileUpdated = "Profile updated",
    enterFirstName = "Enter first name",
    enterLastName = "Enter last name"
)

val TurkishStrings = StringResources(
    settings = "Ayarlar",
    profile = "Profil",
    editProfile = "Profili Düzenle",
    firstName = "Ad",
    lastName = "Soyad",
    save = "Kaydet",
    cancel = "İptal",
    appearance = "Görünüm",
    systemDefault = "Sistem Varsayılanı",
    followSystemTheme = "Sistem temasını takip et",
    lightMode = "Açık Mod",
    alwaysLightTheme = "Her zaman açık tema kullan",
    darkMode = "Koyu Mod",
    alwaysDarkTheme = "Her zaman koyu tema kullan",
    language = "Dil",
    selectLanguage = "Dil seçin",
    currency = "Para Birimi",
    selectCurrency = "Para birimi seçin",
    notifications = "Bildirimler",
    enableNotifications = "Bildirimleri etkinleştir",
    priceAlerts = "Fiyat Uyarıları",
    getPriceAlertNotifications = "Hedef fiyatlara ulaşıldığında bildirim al",
    about = "Hakkında",
    version = "Sürüm",
    termsOfService = "Kullanım Şartları",
    privacyPolicy = "Gizlilik Politikası",
    rateApp = "Uygulamayı Değerlendir",
    portfolio = "Portföy",
    totalValue = "Toplam Değer",
    cashBalance = "Nakit Bakiye",
    discoverCoins = "Kripto Keşfet",
    history = "Geçmiş",
    analytics = "Analitik",
    alerts = "Uyarılar",
    buy = "Al",
    sell = "Sat",
    availableBalance = "Kullanılabilir Bakiye",
    amountOwned = "Sahip Olunan Miktar",
    transactionHistory = "İşlem Geçmişi",
    noTransactions = "Henüz işlem yok",
    back = "Geri",
    general = "Genel",
    preferences = "Tercihler",
    tapToEdit = "Düzenlemek için dokunun",
    profileUpdated = "Profil güncellendi",
    enterFirstName = "Adınızı girin",
    enterLastName = "Soyadınızı girin"
)

fun getStringResources(language: Language): StringResources {
    return when (language) {
        Language.ENGLISH -> EnglishStrings
        Language.TURKISH -> TurkishStrings
    }
}

val LocalStringResources = staticCompositionLocalOf { EnglishStrings }

@Composable
fun ProvideStringResources(
    language: Language,
    content: @Composable () -> Unit
) {
    val strings = getStringResources(language)
    CompositionLocalProvider(LocalStringResources provides strings) {
        content()
    }
}


