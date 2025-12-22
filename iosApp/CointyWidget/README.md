# iOS Widget Extension Setup Guide

iOS widget'ları aktifleştirmek için Xcode'da Widget Extension target eklemeniz gerekiyor.

## Adımlar

### 1. Widget Extension Target Ekleme

1. Xcode'da `iosApp.xcodeproj` dosyasını açın
2. File → New → Target seçin
3. "Widget Extension" seçin ve Next'e tıklayın
4. Ayarları girin:
   - **Product Name:** `CointyWidget`
   - **Bundle Identifier:** `com.kaaneneskpc.cointy.widget`
   - **Include Configuration Intent:** İşaretsiz bırakın
5. Finish'e tıklayın
6. Oluşturulan dosyaları silin ve mevcut `CointyWidget/` klasöründeki dosyaları kullanın

### 2. App Groups Yapılandırması

Widget ile ana uygulama arasında veri paylaşımı için:

1. Ana hedef (iosApp) seçin → Signing & Capabilities
2. "+ Capability" → "App Groups" ekleyin
3. `group.com.kaaneneskpc.cointy` grubunu ekleyin
4. Widget hedefinde aynı adımları tekrarlayın

### 3. Info.plist Güncellemesi

Ana uygulamanın `Info.plist` dosyasına şunu ekleyin (zaten ekli olması gereken):

```xml
<key>BGTaskSchedulerPermittedIdentifiers</key>
<array>
    <string>com.kaaneneskpc.cointy.pricealert.refresh</string>
</array>
```

### 4. Swift Dosyalarını Ekleme

Widget target'a şu dosyaları ekleyin:
- `CointyWidget/PortfolioWidget.swift`
- `CointyWidget/CoinPriceWidget.swift`
- `CointyWidget/CointyWidgetBundle.swift`
- `CointyWidget/Assets.xcassets`
- `CointyWidget/Info.plist`

### 5. Build & Run

1. Widget hedefini seçin ve build edin
2. Simulatör/cihazda ana uygulamayı çalıştırın
3. Ana ekrana uzun basın → Widgets → "Cointy" bul
4. Widget'ları ekleyin

## Dosya Yapısı

```
iosApp/
├── CointyWidget/
│   ├── Assets.xcassets/
│   │   ├── Contents.json
│   │   ├── AppIcon.appiconset/
│   │   │   └── Contents.json
│   │   └── WidgetBackground.colorset/
│   │       └── Contents.json
│   ├── CointyWidgetBundle.swift
│   ├── PortfolioWidget.swift
│   ├── CoinPriceWidget.swift
│   └── Info.plist
├── iosApp/
│   └── ...
└── iosApp.xcodeproj/
```

## Notlar

- Widget verisi uygulama her açıldığında güncellenir
- Widget'lar 30 dakikada bir otomatik güncellenir
- App Groups olmadan widget veri gösteremez
