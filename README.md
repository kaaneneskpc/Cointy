<div align="center">

# 💰 Cointy

### Cross-Platform Cryptocurrency Portfolio Management App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![iOS](https://img.shields.io/badge/iOS-13.0+-000000?style=for-the-badge&logo=ios&logoColor=white)](https://www.apple.com/ios/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

*Your intelligent companion for managing cryptocurrency portfolios, tracking investments, and exploring the crypto market across Android and iOS.*

[Features](#-features) • [Tech Stack](#-tech-stack) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [Screenshots](#-screenshots)

---

</div>

## 🎯 About

**Cointy** is a modern, cross-platform mobile application built with Kotlin Multiplatform that enables users to manage their cryptocurrency portfolios, discover new coins, and perform virtual buy/sell transactions. With a clean architecture and beautiful Material Design 3 UI, Cointy provides a seamless experience for both Android and iOS users.

### Why Cointy?

- 📱 **Cross-Platform**: Single codebase for Android and iOS using Kotlin Multiplatform
- 🔐 **Secure**: Biometric authentication for enhanced security
- 📊 **Real-Time Data**: Live cryptocurrency prices and market data via CoinRanking API
- 💼 **Portfolio Management**: Track your investments with detailed performance metrics
- 📈 **Price Charts**: Visualize price trends with interactive sparkline charts
- 💰 **Virtual Trading**: Practice buying and selling cryptocurrencies without real money
- 🔔 **Price Alerts**: Set alerts for price targets and get notified
- 🔍 **Search & Filter**: Find coins quickly with powerful search and filtering
- 📊 **Analytics**: Detailed portfolio analytics with charts and statistics
- 🎨 **Modern UI**: Beautiful Material Design 3 interface with smooth animations
- 🌙 **Dark Mode**: Full dark mode support with system, light, and dark theme options
- 💾 **Offline Support**: Local database for offline access to your portfolio

## ✨ Features

### 🏦 Core Features

- **Portfolio Dashboard**
  - View total portfolio value and cash balance
  - Track all owned cryptocurrencies
  - Performance percentage with color-coded indicators
  - Average purchase price tracking
  - Real-time portfolio value calculations

- **Coin Discovery**
  - Browse comprehensive list of cryptocurrencies
  - View current prices and 24h change percentages
  - Search and explore new investment opportunities
  - Long-press to view price history charts

- **Buy & Sell**
  - Virtual cryptocurrency trading
  - Buy coins with available cash balance
  - Sell coins from your portfolio
  - Automatic average purchase price calculation (DCA)
  - Insufficient funds validation

- **Price Charts**
  - Interactive sparkline charts
  - Historical price data visualization
  - Track price trends over time
  - Quick access via long-press gesture

- **Biometric Security**
  - Face ID / Touch ID authentication (iOS)
  - Fingerprint / Face unlock (Android)
  - Secure app access on launch
  - Platform-native biometric implementation

- **Cash Balance Management**
  - Track virtual cash balance
  - Automatic balance updates on transactions
  - Initial balance setup
  - Real-time balance display

- **🔔 Price Alerts & Notifications**
  - Create price alerts for any cryptocurrency
  - Set target price with condition (Above/Below)
  - Enable/disable alerts with toggle
  - Platform-native notifications (Android/iOS)
  - Track triggered alerts history
  - Automatic alert checking on coin list refresh

- **🔍 Search & Filter**
  - Real-time coin search by name or symbol
  - Filter coins by performance (All, Gainers, Losers)
  - Sort coins by name, price, or 24h change
  - Transaction history search and filtering
  - Filter transactions by type (Buy/Sell)
  - Sort transactions by date or amount

- **📊 Portfolio Analytics**
  - Total portfolio value and profit/loss summary
  - Interactive pie chart for coin distribution
  - Portfolio history line chart with trends
  - Transaction statistics (total, buy, sell counts)
  - Individual coin performance tracking
  - Color-coded profit/loss indicators

- **📜 Transaction History**
  - Complete buy/sell transaction records
  - Chronological transaction listing
  - Detailed transaction information
  - Search transactions by coin name
  - Filter by transaction type
  - Sort by date or amount

- **🌙 Dark Mode & Settings**
  - Three theme options: System, Light, Dark
  - Real-time theme switching
  - Accessible from Portfolio screen
  - Consistent design across all themes
  - Optimized colors for both light and dark modes

## 🛠 Tech Stack

### Multiplatform & Language
- **Language**: Kotlin 2.0.21
- **Platform**: Kotlin Multiplatform (KMP)
- **Min SDK (Android)**: 24
- **Target SDK (Android)**: 35
- **iOS**: 13.0+

### UI Framework
- **Compose Multiplatform**: Shared UI across platforms
- **Material Design 3**: Modern design system
- **Coil**: Image loading and caching
- **Compose Navigation**: Type-safe navigation

### Architecture & Patterns
- **Clean Architecture**: Separation of concerns with multiple layers
- **MVVM Pattern**: ViewModel for UI state management
- **Repository Pattern**: Data abstraction layer
- **Use Cases**: Single responsibility business logic
- **Flow/StateFlow**: Reactive data streams

### Dependency Injection
- **Koin**: Lightweight dependency injection framework
- **Koin Compose**: Compose integration
- **Koin ViewModel**: ViewModel injection

### Data & Networking
- **Room Database**: Local data persistence (SQLite)
- **Ktor Client**: Modern HTTP client
- **Kotlinx Serialization**: JSON serialization/deserialization
- **CoinRanking API**: Cryptocurrency data provider

### Asynchronous Programming
- **Kotlin Coroutines**: Asynchronous programming
- **Flow**: Reactive data streams
- **StateFlow**: State management
- **Channel**: Event handling

### Other Libraries
- **Kotlinx DateTime**: Date and time handling
- **AndroidX Biometric**: Biometric authentication
- **KSP**: Kotlin Symbol Processing

## 🏗 Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

```
📦 Cointy
├── 📱 composeApp                    # Main application module
│   ├── commonMain/                  # Shared code for all platforms
│   │   ├── biometric/               # Biometric authentication
│   │   ├── coins/                   # Coin discovery feature
│   │   │   ├── data/                # Data layer
│   │   │   │   ├── mapper/          # Data mappers
│   │   │   │   └── remote/          # Remote data source
│   │   │   │       ├── dto/         # Data transfer objects
│   │   │   │       └── impl/        # API implementation
│   │   │   ├── domain/              # Domain layer
│   │   │   │   ├── api/             # Repository interfaces
│   │   │   │   ├── model/           # Domain models
│   │   │   │   └── UseCases         # Business logic
│   │   │   └── presentation/        # Presentation layer
│   │   │       ├── component/       # UI components
│   │   │       └── ViewModels       # State management
│   │   ├── core/                    # Core modules
│   │   │   ├── biometric/           # Biometric abstraction
│   │   │   ├── database/            # Database setup
│   │   │   ├── domain/              # Core domain models
│   │   │   ├── navigation/          # Navigation routes
│   │   │   ├── network/             # Network configuration
│   │   │   ├── notification/        # Notification services
│   │   │   └── util/                # Utility functions
│   │   ├── di/                      # Dependency injection
│   │   ├── portfolio/               # Portfolio management
│   │   ├── analytics/               # Portfolio analytics
│   │   ├── transaction/             # Transaction history
│   │   ├── alert/                   # Price alerts
│   │   ├── settings/                # Settings and theme preferences
│   │   ├── theme/                   # UI theme and styles
│   │   └── trade/                   # Buy/Sell functionality
│   ├── androidMain/                 # Android-specific code
│   └── iosMain/                     # iOS-specific code
│
└── 📱 iosApp/                       # iOS application entry point
```

### Layer Responsibilities

#### 🎨 Presentation Layer
- **Composables**: UI components (Compose)
- **ViewModels**: UI state management with StateFlow
- **UI Models**: UI-specific data models
- **Events**: User interaction handling

#### 🧠 Domain Layer
- **Use Cases**: Single-responsibility business logic
  - `GetCoinsListUseCase`
  - `GetCoinDetailsUseCase`
  - `GetCoinPriceHistoryUseCase`
  - `BuyCoinUseCase`
  - `SellCoinUseCase`
  - `GetTransactionHistoryUseCase`
  - `GetPortfolioAnalyticsUseCase`
  - `CreatePriceAlertUseCase`
  - `GetPriceAlertsUseCase`
  - `DeletePriceAlertUseCase`
  - `TogglePriceAlertUseCase`
  - `CheckPriceAlertsUseCase`
- **Repository Interfaces**: Data abstraction
- **Domain Models**: Core business entities

#### 💾 Data Layer
- **Repositories**: Data sources coordination
- **Remote Data Source**: API calls via Ktor
- **Local Data Source**: Room Database queries
- **Mappers**: Entity transformations
- **DTOs & Entities**: Data transfer objects

### Database Schema

**PortfolioDatabase (Version 4)**
- `PortfolioCoinEntity`: User's cryptocurrency holdings
- `UserBalanceEntity`: User's cash balance
- `TransactionEntity`: Buy/sell transaction records
- `PriceAlertEntity`: User's price alerts

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Hedgehog | 2023.1.1 or newer
- **Xcode**: 14.0+ (for iOS development)
- **JDK**: 17 or higher
- **Kotlin**: 2.0.21
- **Android SDK**: API 24+
- **CocoaPods**: Latest version (for iOS)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/kaaneneskpc/Cointy.git
cd Cointy
```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the project**
```bash
# For Android
./gradlew :composeApp:assembleDebug

# For iOS (macOS only)
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

4. **Run the app**

   **Android:**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use the command line:
   ```bash
   ./gradlew :composeApp:installDebug
   ```

   **iOS:**
   - Open `iosApp/iosApp.xcodeproj` in Xcode
   - Select your target device or simulator
   - Click the "Run" button in Xcode

### Configuration

The app uses the CoinRanking API for cryptocurrency data. No API key is required for basic usage, but you may need to configure rate limiting if you plan to use it extensively.

## 📱 Screenshots

### Android

<div align="center">

| Portfolio | Coin Discovery | Buy Screen | Sell Screen | Price Chart | Transaction History | Portfolio History | Notification & Price Alerts | Search & Filter |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/4e98becf-ac71-4680-abcf-bf190efedea2" width="200"/> | <img src="https://github.com/user-attachments/assets/738334a6-728c-4f04-86c5-5e364f5c98e2" width="200"/> | <img src="https://github.com/user-attachments/assets/cdf6ae97-eeb9-41f3-91f5-b953c3f02cd3" width="200"/> | <img src="https://github.com/user-attachments/assets/c9b46e5c-3712-4b16-92e6-549b8ec4a617" width="200"/> | <img src="https://github.com/user-attachments/assets/ca9d53c1-48c5-44cf-b752-4cc6ec6e5a52" width="200"/> | <img src="https://github.com/user-attachments/assets/e8b7271f-1c9d-4d29-9531-8421b11eb8d6" width="200"/> | <img src="https://github.com/user-attachments/assets/8735d96c-f089-4480-a386-091081b25929" width="200"/> | <table><tr><td> <img src="https://github.com/user-attachments/assets/ab387b01-aa74-4f50-b70b-f3ad5d7f4653" width="100"/> <img src="https://github.com/user-attachments/assets/4883e7d8-092d-4595-b9bf-7ddd2f0b407b" width="100"/></td></tr></table> | <table><tr><td> <img src="https://github.com/user-attachments/assets/6ac37ed7-b189-4d4e-9457-862bc9152e9d" width="100"/> <img src="https://github.com/user-attachments/assets/8a60298e-1112-4a23-a68b-bee4dc0136a2" width="100"/></td></tr></table> | 

</div>

### IOS

<div align="center">

| Portfolio | Coin Discovery | Buy Screen | Sell Screen | Price Chart | Transaction History | Portfolio History | Notification & Price Alerts | Search & Filter |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| <img src="https://github.com/user-attachments/assets/7ae637d6-b276-4a52-b849-e271eebe5269" width="200"/> | <img src="https://github.com/user-attachments/assets/4855272f-6f1a-47f7-9155-3ff72bd4dee2" width="200"/> | <img src="https://github.com/user-attachments/assets/ec6b0de0-1ed1-4a0d-9aa5-18576dada854" width="200"/> | <img src= "https://github.com/user-attachments/assets/5c57520c-fa0d-4ca2-b1e4-605a205c3fff" width="200"/> | <img src="https://github.com/user-attachments/assets/e866369d-9d6b-4748-9c63-a5e79fb12970" width="200"/> | <img src="https://github.com/user-attachments/assets/d646edb7-f00d-4222-945d-da068a23e0dd" width="200"/> | <img src="https://github.com/user-attachments/assets/1fd7deea-b2f8-4afe-ba74-a92ef4f801a3" width="200"/> |  <table><tr><td><img src="https://github.com/user-attachments/assets/2a65d11c-1fc1-42e5-838d-23708abf8bad" width="100"/> <img src="https://github.com/user-attachments/assets/50d56405-6a19-480b-9125-5cc6251bee31" width="100"/></td></tr></table> | <table><tr><td><img src="https://github.com/user-attachments/assets/9306e199-955e-49c7-888b-6dc1b8eef491" width="100"/> <img src="https://github.com/user-attachments/assets/3e520688-77b1-4a85-a478-312dd178edfc" width="100"/></td></tr></table> |

</div>


## 🎮 How to Use

1. **Authenticate**
   - Launch the app
   - Use biometric authentication (Face ID/Touch ID or Fingerprint)
   - Access your portfolio dashboard

2. **View Portfolio**
   - See your total portfolio value
   - Check your cash balance
   - Browse owned cryptocurrencies
   - View performance percentages

3. **Discover Coins**
   - Tap "Discover Coins" to browse all cryptocurrencies
   - View current prices and 24h changes
   - Long-press any coin to see price chart
   - Tap a coin to buy it

4. **Buy Cryptocurrency**
   - Select a coin from the discovery screen
   - Enter the amount you want to invest (in fiat currency)
   - Tap "Buy" to complete the transaction
   - Your portfolio and cash balance will update automatically

5. **Sell Cryptocurrency**
   - Tap any coin in your portfolio
   - Enter the amount you want to sell (in fiat currency)
   - Tap "Sell" to complete the transaction
   - Your portfolio and cash balance will update automatically

6. **View Price Charts**
   - Long-press any coin in the discovery list
   - View historical price trends
   - Dismiss the chart by tapping outside

7. **Search & Filter Coins**
   - Use the search bar to find coins by name or symbol
   - Filter by performance: All, Gainers, or Losers
   - Sort by name, price, or 24h change
   - Results update in real-time as you type

8. **Set Price Alerts**
   - Tap the bell icon on any coin in the discovery list
   - Set target price and condition (Above/Below)
   - Manage alerts from the Alerts screen
   - Enable/disable alerts with toggle
   - Receive notifications when price targets are hit

9. **View Transaction History**
   - Access from the Portfolio screen
   - Search transactions by coin name
   - Filter by type: All, Buy, or Sell
   - Sort by date or amount
   - View detailed transaction information

10. **View Portfolio Analytics**
    - Access from the Portfolio screen
    - View profit/loss summary
    - Explore coin distribution pie chart
    - Track portfolio history over time
    - See individual coin performance

11. **Customize Theme**
    - Tap the settings icon (⚙️) on the Portfolio screen
    - Choose your preferred theme:
      - System Default: Follows your device settings
      - Light Mode: Always use light theme
      - Dark Mode: Always use dark theme
    - Theme changes apply immediately

## 🧪 Testing

Run unit tests:
```bash
./gradlew :composeApp:testDebugUnitTest
```

Run instrumentation tests (Android):
```bash
./gradlew :composeApp:connectedDebugAndroidTest
```

### Test Libraries
- **Kotlin Test**: Unit testing framework
- **AssertK**: Fluent assertion library
- **Turbine**: Flow testing library
- **Coroutines Test**: Testing coroutines
- **Compose UI Test**: UI component testing

## 📋 Project Structure

### Key Modules

- **`coins`**: Coin discovery, listing, search and filter functionality
- **`portfolio`**: Portfolio management and tracking
- **`trade`**: Buy and sell transaction logic
- **`transaction`**: Transaction history with search and filter
- **`analytics`**: Portfolio analytics and charts
- **`alert`**: Price alerts and notifications
- **`settings`**: Settings and theme preferences (dark mode)
- **`biometric`**: Platform-specific biometric authentication
- **`core`**: Shared utilities, database, network, and notification services
- **`theme`**: UI theming and design system

### Navigation Flow

```
Biometric → Portfolio → Coins → Buy
                │         │
                │         └── Create Alert
                │
                ├── Sell
                │
                ├── Transaction History
                │
                ├── Analytics
                │
                ├── Price Alerts
                │
                └── Settings (Theme)
```

## 🔒 Security

- **Biometric Authentication**: Platform-native biometric security
- **Local Database**: Secure local storage with Room
- **HTTPS**: All network requests use secure connections
- **Data Privacy**: No user data is shared with third parties

## 🌐 API Integration

**CoinRanking API**
- Base URL: `https://api.coinranking.com/v2`
- Endpoints:
  - `GET /coins` - List all cryptocurrencies
  - `GET /coin/{coinId}` - Get coin details
  - `GET /coin/{coinId}/history` - Get price history

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

### How to Contribute

1. **Fork the Project**
2. **Create your Feature Branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your Changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the Branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Contribution Guidelines

- Follow the existing code style and architecture
- Write meaningful commit messages
- Add tests for new features
- Update documentation when needed
- Ensure all tests pass before submitting PR
- Follow Clean Architecture principles

## 📋 Project Roadmap

- [x] Cross-platform setup (Android & iOS)
- [x] Portfolio management
- [x] Coin discovery and listing
- [x] Buy/Sell functionality
- [x] Price charts
- [x] Biometric authentication
- [x] Local database persistence
- [x] Transaction history
- [x] Price alerts and notifications
- [x] Portfolio analytics and charts
- [x] Search and filter functionality
- [x] Dark mode support
- [ ] Multi-currency support (USD, EUR, TRY, etc.)
- [ ] Export portfolio data
- [ ] Cloud sync
- [x] Favorites/watchlist
- [ ] Multi-language support

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Kaan Enes Kapıcı**

- GitHub: [@kaaneneskpc](https://github.com/kaaneneskpc)
- LinkedIn: [Kaan Enes Kapıcı](https://www.linkedin.com/in/kaaneneskpc)
- Email: kaaneneskpc1@gmail.com

## 🙏 Acknowledgments

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) for enabling cross-platform development
- [Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) for the amazing UI toolkit
- [Material Design 3](https://m3.material.io/) for design guidelines
- [CoinRanking API](https://developers.coinranking.com/) for cryptocurrency data
- All contributors who help improve this project

## 📞 Support

If you have any questions or need help, feel free to:

- Open an [issue](https://github.com/kaaneneskpc/Cointy/issues)
- Start a [discussion](https://github.com/kaaneneskpc/Cointy/discussions)
- Reach out via email

## ⭐ Show Your Support

Give a ⭐️ if this project helped you!

<div align="center">

### Made with ❤️ and ☕

**Happy Trading! 💰📈**

</div>
