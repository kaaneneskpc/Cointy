# Cointy - Product Requirements Document (PRD)

## 1. Genel Bakış

### 1.1 Ürün Tanımı
**Cointy**, kullanıcıların kripto para portföylerini yönetmelerine, kripto paraları keşfetmelerine ve sanal alım-satım işlemleri yapmalarına olanak tanıyan çok platformlu (Android ve iOS) bir mobil uygulamadır.

### 1.2 Ürün Vizyonu
Kullanıcıların kripto para yatırımlarını takip etmelerini, portföy performanslarını izlemelerini ve kripto para piyasasını keşfetmelerini kolaylaştıran, güvenli ve kullanıcı dostu bir platform sunmak.

### 1.3 Hedef Kitle
- Kripto para yatırımcıları
- Kripto para piyasasını öğrenmek isteyenler
- Portföy yönetimi yapan kullanıcılar
- Sanal alım-satım deneyimi arayanlar

---

## 2. Teknik Mimari

### 2.1 Platform ve Teknolojiler
- **Platform:** Kotlin Multiplatform (KMP)
- **Hedef Platformlar:** Android (minSdk 24), iOS (iOS 13+)
- **UI Framework:** Jetpack Compose Multiplatform
- **Mimari Desen:** Clean Architecture (Domain, Data, Presentation katmanları)
- **Dependency Injection:** Koin
- **Veritabanı:** Room Database (SQLite)
- **Network:** Ktor Client
- **Image Loading:** Coil 3
- **Serialization:** Kotlinx Serialization
- **Date/Time:** Kotlinx DateTime
- **Biometric Auth:** AndroidX Biometric

### 2.2 API Entegrasyonu
- **API Provider:** CoinRanking API (https://api.coinranking.com/v2)
- **Kullanılan Endpointler:**
  - `GET /coins` - Tüm kripto paraların listesi
  - `GET /coin/{coinId}` - Belirli bir kripto para detayı
  - `GET /coin/{coinId}/history` - Kripto para fiyat geçmişi

### 2.3 Veritabanı Şeması
**PortfolioDatabase (Version 3)**
- **PortfolioCoinEntity:** Kullanıcının sahip olduğu kripto paralar
  - coinId (Primary Key)
  - name, symbol, iconUrl
  - averagePurchasePrice (ortalama alış fiyatı)
  - amountOwned (sahip olunan miktar)
  - timeStamp
  
- **UserBalanceEntity:** Kullanıcının nakit bakiyesi
  - id (Primary Key, default: 1)
  - cashBalance (nakit bakiye)

- **TransactionEntity:** Kullanıcının yaptığı alım-satım işlemleri
  - id (Primary Key, autoGenerate)
  - type (String: "BUY" veya "SELL")
  - coinId, coinName, coinSymbol, coinIconUrl
  - amountInFiat (fiat cinsinden işlem tutarı)
  - amountInUnit (coin birimi cinsinden miktar)
  - price (işlem anındaki coin fiyatı)
  - timestamp (işlem zamanı)

---

## 3. Özellikler ve Fonksiyonellik

### 3.1 Biometric Authentication (Biyometrik Kimlik Doğrulama)
**Amaç:** Uygulama güvenliğini artırmak ve kullanıcı deneyimini iyileştirmek.

**Özellikler:**
- Platform-specific biyometrik kimlik doğrulama (Android: BiometricPrompt, iOS: Face ID/Touch ID)
- Uygulama başlangıcında kimlik doğrulama ekranı
- Kimlik doğrulama başarılı olduğunda portföy ekranına yönlendirme

**Teknik Detaylar:**
- `BiometricAuthenticator` interface ile platform abstraction
- `BiometricScreen` Compose UI bileşeni
- Platform-specific implementasyonlar (androidMain, iosMain)

---

### 3.2 Portfolio Management (Portföy Yönetimi)
**Amaç:** Kullanıcıların sahip oldukları kripto paraları görüntülemesi ve yönetmesi.

**Özellikler:**
- **Portföy Görünümü:**
  - Toplam portföy değeri gösterimi
  - Nakit bakiye gösterimi
  - Sahip olunan kripto paraların listesi
  - Her kripto para için:
    - İsim, sembol, ikon
    - Sahip olunan miktar (birim ve fiat cinsinden)
    - Performans yüzdesi (pozitif/negatif renk kodlaması)
    - Ortalama alış fiyatı

- **Portföy İşlemleri:**
  - Kripto para detayına gitme (satış ekranına yönlendirme)
  - Yeni kripto para keşfetme (coin listesi ekranına yönlendirme)
  - Portföy değerinin gerçek zamanlı hesaplanması

**Teknik Detaylar:**
- `PortfolioViewModel` - StateFlow ile reactive state management
- `PortfolioRepository` - Veritabanı işlemleri ve hesaplamalar
- `PortfolioScreen` - Compose UI
- Flow-based reactive data streams

---

### 3.3 Coin Discovery (Kripto Para Keşfi)
**Amaç:** Kullanıcıların mevcut kripto paraları keşfetmesi ve yeni yatırım fırsatları bulması.

**Özellikler:**
- **Coin Listesi:**
  - Tüm kripto paraların listelenmesi
  - Her coin için:
    - İsim, sembol, ikon
    - Güncel fiyat
    - 24 saatlik değişim yüzdesi (pozitif/negatif renk kodlaması)
  
- **Coin Detayları:**
  - Uzun basma ile fiyat grafiği görüntüleme (sparkline chart)
  - Grafik kapatma özelliği
  - Coin detay sayfasına gitme (alış ekranına yönlendirme)

**Teknik Detaylar:**
- `CoinListViewModel` - API çağrıları ve state yönetimi
- `GetCoinsListUseCase` - Coin listesi çekme use case
- `GetCoinPriceHistoryUseCase` - Fiyat geçmişi çekme use case
- `CoinChart` - Compose chart bileşeni
- Error handling ve loading states

---

### 3.4 Buy Coin (Kripto Para Alma)
**Amaç:** Kullanıcıların sanal olarak kripto para satın alması.

**Özellikler:**
- **Alış Ekranı:**
  - Coin bilgileri (isim, sembol, ikon, güncel fiyat)
  - Mevcut nakit bakiye gösterimi
  - Alış miktarı girişi (fiat cinsinden)
  - Currency formatting ve visual transformation
  - Alış butonu

- **Alış İşlemi:**
  - Yetersiz bakiye kontrolü
  - Coin portföye ekleme veya mevcut coin miktarını güncelleme
  - Ortalama alış fiyatı hesaplama (DCA - Dollar Cost Averaging)
  - Nakit bakiyeden düşme
  - İşlemin transaction history'ye kaydedilmesi
  - Başarılı işlem sonrası portföy ekranına yönlendirme

**Teknik Detaylar:**
- `BuyViewModel` - Alış işlemi state yönetimi
- `BuyCoinUseCase` - İş mantığı ve validasyonlar
- `BuyScreen` - Compose UI
- `CurrencyVisualTransformation` - Para birimi formatlaması
- Event-based navigation (Channel kullanımı)

---

### 3.5 Sell Coin (Kripto Para Satma)
**Amaç:** Kullanıcıların portföylerindeki kripto paraları satması.

**Özellikler:**
- **Satış Ekranı:**
  - Coin bilgileri (isim, sembol, ikon, güncel fiyat)
  - Sahip olunan miktar gösterimi
  - Satış miktarı girişi (fiat cinsinden)
  - Currency formatting
  - Satış butonu

- **Satış İşlemi:**
  - Yetersiz coin kontrolü
  - Coin miktarını güncelleme veya portföyden tamamen çıkarma (threshold: 1 fiat)
  - Nakit bakiyeye ekleme
  - İşlemin transaction history'ye kaydedilmesi
  - Başarılı işlem sonrası portföy ekranına yönlendirme

**Teknik Detaylar:**
- `SellViewModel` - Satış işlemi state yönetimi
- `SellCoinUseCase` - İş mantığı ve validasyonlar
- `SellScreen` - Compose UI
- Otomatik coin temizleme mantığı

---

### 3.6 Price Charts (Fiyat Grafikleri)
**Amaç:** Kullanıcıların kripto para fiyat trendlerini görselleştirmesi.

**Özellikler:**
- Sparkline chart gösterimi
- Coin listesinde uzun basma ile grafik açma
- Zaman bazlı fiyat verileri
- Loading ve error states

**Teknik Detaylar:**
- `CoinChart` - Compose chart bileşeni
- `GetCoinPriceHistoryUseCase` - API'den fiyat geçmişi çekme
- Timestamp bazlı veri sıralama

---

### 3.7 Cash Balance Management (Nakit Bakiye Yönetimi)
**Amaç:** Kullanıcının sanal nakit bakiyesini yönetmesi.

**Özellikler:**
- İlk açılışta bakiye başlatma
- Alış işlemlerinde bakiyeden düşme
- Satış işlemlerinde bakiyeye ekleme
- Gerçek zamanlı bakiye gösterimi

**Teknik Detaylar:**
- `UserBalanceEntity` - Veritabanı entity
- `UserBalanceDao` - Database Access Object
- Flow-based reactive updates

---

### 3.8 Transaction History (İşlem Geçmişi)
**Amaç:** Kullanıcıların yaptıkları tüm alım-satım işlemlerini görüntülemesi ve takip etmesi.

**Özellikler:**
- **İşlem Geçmişi Görünümü:**
  - Tüm işlemlerin kronolojik listesi (en yeni en üstte)
  - Toplam işlem sayısı gösterimi
  - Her işlem için:
    - Coin bilgileri (isim, sembol, ikon)
    - İşlem tipi (BUY/SELL) - renk kodlamalı (yeşil/kırmızı)
    - İşlem tutarı (fiat cinsinden)
    - Coin miktarı ve birim fiyatı
    - İşlem tarihi ve saati (formatlanmış)
  
- **İşlem Kaydetme:**
  - Her alış işleminde otomatik kayıt
  - Her satış işleminde otomatik kayıt
  - İşlem detaylarının tam olarak saklanması

- **Boş Durum:**
  - İşlem yoksa kullanıcı dostu boş durum mesajı

**Teknik Detaylar:**
- `TransactionEntity` - Veritabanı entity
- `TransactionDao` - Database Access Object
- `TransactionRepository` - Repository interface ve implementasyonu
- `GetTransactionHistoryUseCase` - İşlem geçmişi çekme use case
- `TransactionHistoryViewModel` - StateFlow ile reactive state management
- `TransactionHistoryScreen` - Compose UI
- Flow-based reactive data streams
- Timestamp bazlı sıralama (DESC)
- Coin bazlı filtreleme desteği (getTransactionsByCoinId)

---

## 4. Kullanıcı Akışları (User Flows)

### 4.1 Uygulama Başlangıç Akışı
1. Uygulama açılır
2. Biyometrik kimlik doğrulama ekranı gösterilir
3. Kullanıcı kimlik doğrulamasını tamamlar
4. Portföy ekranına yönlendirilir

### 4.2 Portföy Görüntüleme Akışı
1. Portföy ekranında:
   - Toplam portföy değeri görüntülenir
   - Nakit bakiye görüntülenir
   - Sahip olunan coinler listelenir
2. Coin'e tıklanırsa → Satış ekranına gider
3. "Discover Coins" butonuna tıklanırsa → Coin listesi ekranına gider
4. "History" butonuna tıklanırsa → İşlem geçmişi ekranına gider

### 4.3 Coin Keşfetme ve Alma Akışı
1. Coin listesi ekranında tüm coinler görüntülenir
2. Coin'e uzun basılırsa → Fiyat grafiği gösterilir
3. Coin'e tıklanırsa → Alış ekranına gider
4. Alış ekranında:
   - Coin bilgileri gösterilir
   - Mevcut bakiye gösterilir
   - Alış miktarı girilir
   - "Buy" butonuna tıklanır
5. Başarılı işlem sonrası portföy ekranına dönülür

### 4.4 Coin Satma Akışı
1. Portföy ekranında coin'e tıklanır
2. Satış ekranında:
   - Coin bilgileri gösterilir
   - Sahip olunan miktar gösterilir
   - Satış miktarı girilir
   - "Sell" butonuna tıklanır
3. Başarılı işlem sonrası:
   - İşlem veritabanına kaydedilir
   - Portföy ekranına dönülür

### 4.5 İşlem Geçmişi Görüntüleme Akışı
1. Portföy ekranında "History" butonuna tıklanır
2. İşlem geçmişi ekranında:
   - Tüm işlemler kronolojik sırada listelenir
   - Her işlem için detaylı bilgiler gösterilir
   - İşlem tipi renk kodlamalı gösterilir (BUY: yeşil, SELL: kırmızı)
3. Geri butonuna tıklanırsa → Portföy ekranına dönülür

---

## 5. Teknik Gereksinimler

### 5.1 Minimum Sistem Gereksinimleri
- **Android:**
  - Minimum SDK: 24 (Android 7.0 Nougat)
  - Target SDK: 35 (Android 15)
  - Biometric hardware desteği (opsiyonel)
  
- **iOS:**
  - iOS 13.0+
  - Face ID / Touch ID desteği (opsiyonel)

### 5.2 Bağımlılıklar
- Kotlin 2.0.21
- Compose Multiplatform 1.7.0
- Room 2.7.0-alpha11
- Ktor 3.0.0
- Koin 4.0.0
- Coil 3.0.0
- Kotlinx Serialization 1.7.3
- Kotlinx DateTime 0.6.1

### 5.3 Performans Gereksinimleri
- API çağrıları için timeout yönetimi
- Offline-first yaklaşım (yerel veritabanı)
- Reactive state management (Flow/StateFlow)
- Image caching (Coil)
- Efficient database queries

---

## 6. Hata Yönetimi ve Edge Cases

### 6.1 Network Hataları
- **REQUEST_TIMEOUT:** İstek zaman aşımına uğradığında
- **NO_INTERNET:** İnternet bağlantısı yoksa
- **SERVER_ERROR:** Sunucu hatası (500-599)
- **TOO_MANY_REQUESTS:** Rate limiting (429)
- **SERIALIZATION:** JSON parse hatası
- **UNKNOWN:** Bilinmeyen hatalar

### 6.2 Local Hatalar
- **INSUFFICIENT_FUNDS:** Yetersiz bakiye veya coin miktarı
- Database hataları

### 6.3 Error Handling Stratejisi
- User-friendly error mesajları (`DataErrorToString`)
- Error state'lerin UI'da gösterilimi
- Retry mekanizmaları (gelecekte eklenebilir)
- Graceful degradation

---

## 7. Güvenlik Özellikleri

### 7.1 Biometric Authentication
- Platform-native biyometrik kimlik doğrulama
- Uygulama başlangıcında zorunlu kimlik doğrulama
- Platform-specific implementasyonlar

### 7.2 Veri Güvenliği
- Yerel veritabanı (Room) ile veri saklama
- Network isteklerinde HTTPS kullanımı
- Sensitive data'nın güvenli saklanması

---

## 8. UI/UX Özellikleri

### 8.1 Tasarım Sistemi
- Material Design 3 kullanımı
- Custom theme (`CointyTheme`)
- Custom color palette (`CointyColors`)
- Typography system (`Font`)

### 8.2 Kullanıcı Deneyimi
- Loading states (skeleton screens veya progress indicators)
- Error states (user-friendly mesajlar)
- Empty states
- Smooth navigation transitions
- Currency formatting ve visual transformations
- Color-coded performance indicators (yeşil/kırmızı)

---

## 9. Test Stratejisi

### 9.1 Test Kütüphaneleri
- Kotlin Test
- AssertK (assertion library)
- Turbine (Flow testing)
- Coroutines Test
- Compose UI Test

### 9.2 Test Kapsamı
- Unit tests (Use cases, ViewModels)
- Integration tests (Repository)
- UI tests (Compose screens)
- Flow-based testing

### 9.3 Mevcut Unit Testler

#### 9.3.1 PortfolioViewModelTest
**Konum:** `commonTest/kotlin/com/kaaneneskpc/cointy/portfolio/presentation/PortfolioViewModelTest.kt`

**Test Senaryoları:**
- `State and portfolio coins are properly combined` - State ve portföy coinlerinin doğru birleştirilmesi
- `Portfolio value updates when a coin is added` - Coin eklendiğinde portföy değerinin güncellenmesi
- `Loading state and error message update on failure` - Hata durumunda loading state ve error mesajının güncellenmesi

**Kullanılan Teknikler:**
- Turbine ile Flow testing
- UnconfinedTestDispatcher ile coroutine testing
- FakePortfolioRepository ile dependency mocking

#### 9.3.2 FakePortfolioRepository
**Konum:** `commonTest/kotlin/com/kaaneneskpc/cointy/portfolio/data/FakePortfolioRepository.kt`

**Özellikler:**
- `PortfolioRepository` interface'inin fake implementasyonu
- MutableStateFlow ile reactive test data yönetimi
- `simulateError()` metodu ile hata senaryolarının test edilmesi
- Companion object ile test verileri (fakeCoin, portfolioCoin, cashBalance)

### 9.4 Test Yapısı
```
composeApp/src/
├── commonTest/
│   └── kotlin/com/kaaneneskpc/cointy/
│       ├── ComposeAppCommonTest.kt
│       └── portfolio/
│           ├── data/
│           │   └── FakePortfolioRepository.kt
│           └── presentation/
│               └── PortfolioViewModelTest.kt
└── androidUnitTest/
    └── kotlin/com/kaaneneskpc/cointy/
        └── trade/presentation/
```

---

## 10. Gelecek Geliştirmeler (Future Enhancements)

### 10.1 Önerilen Özellikler
- **Portföy Analitiği:**
  - Detaylı performans grafikleri
  - Kar/zarar analizi
  - Yatırım dağılımı grafikleri
  
- **Bildirimler:**
  - Fiyat alarmları
  - Portföy değeri değişim bildirimleri
  
- **Çoklu Para Birimi Desteği:**
  - USD, EUR, TRY gibi farklı para birimleri
  
  
- **Favoriler:**
  - Coin'leri favorilere ekleme
  - Favori coin listesi
  
- **Arama ve Filtreleme:**
  - Coin arama özelliği
  - Fiyat, değişim gibi kriterlere göre filtreleme
  - İşlem geçmişinde filtreleme (coin bazlı, tarih bazlı, tip bazlı)
  
- **Dark Mode:**
  - Karanlık tema desteği
  
- **Offline Mode:**
  - İnternet olmadan çalışma
  - Cache'lenmiş verilerle çalışma
  
- **Export/Import:**
  - Portföy verilerini export etme
  - İşlem geçmişini export etme (CSV, PDF)
  - Backup ve restore özellikleri

---

## 11. Proje Yapısı

### 11.1 Modül Organizasyonu
```
composeApp/src/
├── commonMain/
│   └── kotlin/com/kaaneneskpc/cointy/
│       ├── App.kt                    # Ana uygulama entry point
│       ├── biometric/                 # Biyometrik kimlik doğrulama
│       ├── coins/                     # Coin keşfetme modülü
│       │   ├── data/                  # Data layer
│       │   ├── domain/                # Domain layer
│       │   └── presentation/         # Presentation layer
│       ├── core/                      # Core utilities ve abstractions
│       │   ├── biometric/
│       │   ├── database/
│       │   ├── domain/
│       │   ├── navigation/
│       │   ├── network/
│       │   └── util/
│       ├── di/                        # Dependency Injection (Koin)
│       ├── portfolio/                 # Portföy yönetimi modülü
│       │   ├── data/
│       │   ├── domain/
│       │   └── presentation/
│       ├── theme/                     # UI tema ve stiller
│       ├── trade/                      # Alım-satım modülü
│       │   ├── domain/
│       │   ├── mapper/
│       │   └── presentation/
│       └── transaction/                # İşlem geçmişi modülü
│           ├── data/
│           │   ├── local/
│           │   └── mapper/
│           ├── domain/
│           └── presentation/
├── androidMain/                       # Android-specific kod
└── iosMain/                           # iOS-specific kod
```

### 11.2 Mimari Katmanları
- **Presentation Layer:** ViewModels, UI Components (Compose)
- **Domain Layer:** Use Cases, Models, Repository Interfaces
- **Data Layer:** Repository Implementations, Data Sources (Remote/Local), Mappers, DTOs

---

## 12. Versiyon Bilgisi

- **Version Code:** 1
- **Version Name:** 1.0
- **Database Version:** 3
- **Kotlin Version:** 2.0.21
- **Compose Multiplatform:** 1.7.0

---

## 13. Notlar ve Önemli Kararlar

### 13.1 Mimari Kararlar
- Clean Architecture prensiplerine uyum
- MVVM pattern kullanımı
- Repository pattern ile data abstraction
- Use Case pattern ile business logic separation

### 13.2 Teknoloji Seçimleri
- **Kotlin Multiplatform:** Kod paylaşımı için
- **Compose Multiplatform:** Modern, declarative UI
- **Room Database:** Güvenilir, performanslı yerel veri saklama
- **Ktor:** Modern, coroutine-based network library
- **Koin:** Hafif, kolay kullanımlı DI framework

### 13.3 API Entegrasyonu
- CoinRanking API kullanımı
- RESTful API pattern
- JSON serialization (Kotlinx Serialization)
- Error handling ve retry mekanizmaları

---

## 14. Dokümantasyon ve Kaynaklar

### 14.1 İlgili Dokümantasyonlar
- Kotlin Multiplatform: https://kotlinlang.org/docs/multiplatform.html
- Compose Multiplatform: https://www.jetbrains.com/lp/compose-multiplatform/
- Room Database: https://developer.android.com/training/data-storage/room
- Ktor: https://ktor.io/
- CoinRanking API: https://developers.coinranking.com/

### 14.2 Proje İçi Dokümantasyon
- README.md - Proje kurulum ve build talimatları
- Bu PRD dokümantasyonu

---

**Son Güncelleme:** 2025
**Dokümantasyon Versiyonu:** 1.0

