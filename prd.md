# Cointy - Product Requirements Document (PRD)

## 1. Overview

### 1.1 Product Definition
**Cointy** is a cross-platform (Android and iOS) mobile application that allows users to manage their cryptocurrency portfolios, discover cryptocurrencies, and perform virtual buy/sell transactions.

### 1.2 Product Vision
To provide a secure and user-friendly platform that makes it easy for users to track their cryptocurrency investments, monitor portfolio performance, and explore the cryptocurrency market.

### 1.3 Target Audience
- Cryptocurrency investors
- People who want to learn about the cryptocurrency market
- Users who manage portfolios
- Those seeking virtual trading experience

---

## 2. Technical Architecture

### 2.1 Platform and Technologies
- **Platform:** Kotlin Multiplatform (KMP)
- **Target Platforms:** Android (minSdk 24), iOS (iOS 13+)
- **UI Framework:** Jetpack Compose Multiplatform
- **Architectural Pattern:** Clean Architecture (Domain, Data, Presentation layers)
- **Dependency Injection:** Koin
- **Database:** Room Database (SQLite)
- **Network:** Ktor Client
- **Image Loading:** Coil 3
- **Serialization:** Kotlinx Serialization
- **Date/Time:** Kotlinx DateTime
- **Biometric Auth:** AndroidX Biometric

### 2.2 API Integration
- **API Provider:** CoinRanking API (https://api.coinranking.com/v2)
- **Used Endpoints:**
  - `GET /coins` - List of all cryptocurrencies
  - `GET /coin/{coinId}` - Specific cryptocurrency details
  - `GET /coin/{coinId}/history` - Cryptocurrency price history

### 2.3 Database Schema
**PortfolioDatabase (Version 4)**
- **PortfolioCoinEntity:** Cryptocurrencies owned by the user
  - coinId (Primary Key)
  - name, symbol, iconUrl
  - averagePurchasePrice (average purchase price)
  - amountOwned (amount owned)
  - timeStamp
  
- **UserBalanceEntity:** User's cash balance
  - id (Primary Key, default: 1)
  - cashBalance (cash balance)

- **TransactionEntity:** User's buy/sell transactions
  - id (Primary Key, autoGenerate)
  - type (String: "BUY" or "SELL")
  - coinId, coinName, coinSymbol, coinIconUrl
  - amountInFiat (transaction amount in fiat)
  - amountInUnit (amount in coin units)
  - price (coin price at transaction time)
  - timestamp (transaction time)

- **PriceAlertEntity:** User's price alerts
  - id (Primary Key, autoGenerate)
  - coinId, coinName, coinSymbol, coinIconUrl
  - targetPrice (target price for alert)
  - condition (String: "ABOVE" or "BELOW")
  - isEnabled (alert active status)
  - isTriggered (whether alert has been triggered)
  - createdAt (creation timestamp)
  - triggeredAt (trigger timestamp, nullable)

---

## 3. Features and Functionality

### 3.1 Biometric Authentication
**Purpose:** To enhance application security and improve user experience.

**Features:**
- Platform-specific biometric authentication (Android: BiometricPrompt, iOS: Face ID/Touch ID)
- Authentication screen at application startup
- Redirect to portfolio screen upon successful authentication

**Technical Details:**
- `BiometricAuthenticator` interface for platform abstraction
- `BiometricScreen` Compose UI component
- Platform-specific implementations (androidMain, iosMain)

---

### 3.2 Portfolio Management
**Purpose:** For users to view and manage their owned cryptocurrencies.

**Features:**
- **Portfolio View:**
  - Total portfolio value display
  - Cash balance display
  - List of owned cryptocurrencies
  - For each cryptocurrency:
    - Name, symbol, icon
    - Amount owned (in units and fiat)
    - Performance percentage (positive/negative color coding)
    - Average purchase price

- **Portfolio Operations:**
  - Navigate to cryptocurrency details (redirect to sell screen)
  - Discover new cryptocurrencies (redirect to coin list screen)
  - Real-time portfolio value calculation

**Technical Details:**
- `PortfolioViewModel` - Reactive state management with StateFlow
- `PortfolioRepository` - Database operations and calculations
- `PortfolioScreen` - Compose UI
- Flow-based reactive data streams

---

### 3.3 Coin Discovery
**Purpose:** For users to discover available cryptocurrencies and find new investment opportunities.

**Features:**
- **Coin List:**
  - Listing of all cryptocurrencies
  - For each coin:
    - Name, symbol, icon
    - Current price
    - 24-hour change percentage (positive/negative color coding)
  
- **Coin Details:**
  - View price chart with long press (sparkline chart)
  - Chart close feature
  - Navigate to coin detail page (redirect to buy screen)

**Technical Details:**
- `CoinListViewModel` - API calls and state management
- `GetCoinsListUseCase` - Coin list fetching use case
- `GetCoinPriceHistoryUseCase` - Price history fetching use case
- `CoinChart` - Compose chart component
- Error handling and loading states

---

### 3.4 Buy Coin
**Purpose:** For users to virtually purchase cryptocurrencies.

**Features:**
- **Buy Screen:**
  - Coin information (name, symbol, icon, current price)
  - Available cash balance display
  - Purchase amount input (in fiat)
  - Currency formatting and visual transformation
  - Buy button

- **Buy Transaction:**
  - Insufficient balance check
  - Add coin to portfolio or update existing coin amount
  - Average purchase price calculation (DCA - Dollar Cost Averaging)
  - Deduct from cash balance
  - Record transaction in transaction history
  - Redirect to portfolio screen after successful transaction

**Technical Details:**
- `BuyViewModel` - Buy transaction state management
- `BuyCoinUseCase` - Business logic and validations
- `BuyScreen` - Compose UI
- `CurrencyVisualTransformation` - Currency formatting
- Event-based navigation (Channel usage)

---

### 3.5 Sell Coin
**Purpose:** For users to sell cryptocurrencies from their portfolio.

**Features:**
- **Sell Screen:**
  - Coin information (name, symbol, icon, current price)
  - Amount owned display
  - Sell amount input (in fiat)
  - Currency formatting
  - Sell button

- **Sell Transaction:**
  - Insufficient coin check
  - Update coin amount or completely remove from portfolio (threshold: 1 fiat)
  - Add to cash balance
  - Record transaction in transaction history
  - Redirect to portfolio screen after successful transaction

**Technical Details:**
- `SellViewModel` - Sell transaction state management
- `SellCoinUseCase` - Business logic and validations
- `SellScreen` - Compose UI
- Automatic coin cleanup logic

---

### 3.6 Price Charts (TradingView Style)
**Purpose:** For users to visualize cryptocurrency price trends with professional-grade charting similar to TradingView.

**Features:**
- **TradingView-Style Chart Display:**
  - Professional dark theme interface (#131722 background)
  - Interactive crosshair for precise price tracking
  - Animated chart line drawing with smooth transitions
  - Gradient fill under the price line
  - Bezier curve smoothing for data points

- **Price Axis (Right Side):**
  - Dynamic price labels
  - Current price indicator with colored badge
  - Dashed horizontal price lines

- **Time Axis (Bottom):**
  - Time labels in HH:mm format
  - Dashed vertical grid lines
  - Automatic time interval calculation

- **OHLC Data Display:**
  - Open (O) - Opening price
  - High (H) - Highest price (green)
  - Low (L) - Lowest price (red)
  - Close (C) - Current/closing price

- **Interactive Features:**
  - Touch and drag to show crosshair
  - Real-time price and time display on crosshair
  - Tap to dismiss crosshair

- **Chart Statistics:**
  - 24h price change percentage with color coding
  - 24h price range (Low - High)
  - Volume indicator

- **Visual Indicators:**
  - Green color for positive price movement
  - Red color for negative price movement
  - Current price dashed line

**Technical Details:**
- `TradingViewChart` - Main chart Compose component with Canvas drawing
- `ChartDataPoint` - Data class for price and timestamp
- `CoinChart` - Legacy sparkline chart component (still available)
- `GetCoinPriceHistoryUseCase` - Fetch price history from API
- `UiChartState` - Extended state with timestamps and coin symbol
- Timestamp-based data sorting
- Crosshair position tracking with gesture detection
- Animation using `Animatable` with `FastOutSlowInEasing`

**UI Components:**
- `TradingViewChart` - Full-featured TradingView-style chart
- `CrosshairInfoBar` - Price and time display when crosshair is active
- `TradingViewPriceInfo` - OHLC price display component
- `TradingViewStatItem` - Statistics display component

**Color Scheme (TradingView Dark Theme):**
- Background: #131722
- Grid: #2A2E39
- Text: #787B86
- Crosshair: #9598A1
- Accent Blue: #2962FF
- Profit Green: Theme profitGreen
- Loss Red: Theme lossRed

---

### 3.7 Cash Balance Management
**Purpose:** To manage the user's virtual cash balance.

**Features:**
- Initialize balance on first launch
- Deduct from balance on buy transactions
- Add to balance on sell transactions
- Real-time balance display

**Technical Details:**
- `UserBalanceEntity` - Database entity
- `UserBalanceDao` - Database Access Object
- Flow-based reactive updates

---

### 3.8 Transaction History
**Purpose:** For users to view and track all their buy/sell transactions.

**Features:**
- **Transaction History View:**
  - Chronological list of all transactions (newest first)
  - Total transaction count display
  - For each transaction:
    - Coin information (name, symbol, icon)
    - Transaction type (BUY/SELL) - color coded (green/red)
    - Transaction amount (in fiat)
    - Coin amount and unit price
    - Formatted transaction date and time
  
- **Transaction Recording:**
  - Automatic recording on each buy transaction
  - Automatic recording on each sell transaction
  - Complete storage of transaction details

- **Empty State:**
  - User-friendly empty state message when no transactions exist

**Technical Details:**
- `TransactionEntity` - Database entity
- `TransactionDao` - Database Access Object
- `TransactionRepository` - Repository interface and implementation
- `GetTransactionHistoryUseCase` - Transaction history fetching use case
- `TransactionHistoryViewModel` - Reactive state management with StateFlow
- `TransactionHistoryScreen` - Compose UI
- Flow-based reactive data streams
- Timestamp-based sorting (DESC)
- Coin-based filtering support (getTransactionsByCoinId)

---

### 3.9 Portfolio Analytics
**Purpose:** To provide users with detailed analysis and visualization of their portfolio performance.

**Features:**
- **Analytics Summary:**
  - Total portfolio value display
  - Total invested amount display
  - Total profit/loss with color coding (green for profit, red for loss)
  - Profit/loss percentage badge

- **Portfolio Distribution:**
  - Interactive pie chart showing coin allocation
  - Animated chart rendering with smooth transitions
  - Color-coded segments for each cryptocurrency
  - Legend with coin symbols and percentage breakdown
  - Total value display in chart center

- **Portfolio History Chart:**
  - Line chart showing portfolio value over time
  - Animated chart drawing effect
  - Color-coded trend line (green for upward, red for downward)
  - Grid lines for value reference
  - Gradient fill under the line
  - Bezier curve smoothing for data points

- **Transaction Statistics:**
  - Total transaction count
  - Buy transaction count (green)
  - Sell transaction count (red)
  - Visual statistics card

- **Coin Performance:**
  - Individual performance tracking for each owned coin
  - Current value display
  - Profit/loss amount and percentage per coin
  - Sorted by absolute performance percentage
  - Coin icon and details display

- **Empty State:**
  - User-friendly message when no portfolio data exists
  - Guidance to start investing

**Technical Details:**
- `AnalyticsRepository` - Repository interface for analytics data
- `AnalyticsRepositoryImpl` - Implementation with portfolio and transaction data aggregation
- `GetPortfolioAnalyticsUseCase` - Use case for fetching comprehensive analytics
- `AnalyticsViewModel` - State management with StateFlow
- `AnalyticsScreen` - Main analytics Compose UI
- `AnalyticsState` - UI state data class

**Analytics Models:**
- `PortfolioAnalytics` - Comprehensive analytics data model containing:
  - totalPortfolioValue: Current total value of all holdings
  - totalInvestedAmount: Sum of all investment costs
  - totalProfitLoss: Difference between current and invested value
  - profitLossPercentage: Percentage return on investment
  - coinDistributions: List of allocation data per coin
  - portfolioHistory: Time-series data for history chart
  - coinPerformances: Individual coin performance metrics
  - Transaction counts (total, buy, sell)

- `CoinDistribution` - Distribution data for pie chart:
  - coinId, coinName, coinSymbol, coinIconUrl
  - valueInFiat: Current value in fiat currency
  - percentage: Portfolio allocation percentage
  - color: Chart segment color

- `CoinPerformance` - Individual coin performance:
  - coinId, coinName, coinSymbol, coinIconUrl
  - currentValue: Current market value
  - investedAmount: Total invested in this coin
  - profitLoss: Absolute profit/loss
  - profitLossPercentage: Percentage return
  - isPositive: Boolean for color coding

- `PortfolioHistoryPoint` - History chart data point:
  - timestamp: Transaction timestamp
  - totalValue: Portfolio value at that point

**UI Components:**
- `PortfolioPieChart` - Animated donut chart with center text
- `PieChartLegend` - Legend component for pie chart
- `PortfolioHistoryChart` - Animated line chart with gradient fill
- `AnalyticsSummaryCard` - Reusable summary card component
- `TransactionStatsCard` - Transaction statistics display
- `CoinPerformanceItem` - Individual coin performance row

---

## 4. User Flows

### 4.1 Application Startup Flow
1. Application opens
2. Biometric authentication screen is displayed
3. User completes authentication
4. Redirected to portfolio screen

### 4.2 Portfolio Viewing Flow
1. On portfolio screen:
   - Total portfolio value is displayed
   - Cash balance is displayed
   - Owned coins are listed
2. If coin is tapped → Goes to sell screen
3. If "Discover Coins" button is tapped → Goes to coin list screen
4. If "History" button is tapped → Goes to transaction history screen
5. If "Analytics" button is tapped → Goes to portfolio analytics screen
6. If "Alerts" button is tapped → Goes to price alerts screen
7. If settings icon is tapped → Goes to settings screen

### 4.8 Settings and Personalization Flow
1. Settings icon is tapped on portfolio screen (top-right corner)
2. On settings screen:
   - **Profile Section:**
     - User profile card is displayed with avatar and name
     - Tap to edit profile opens dialog
     - Enter first name and last name
     - Save to update profile
   - **General Section:**
     - Language selection (English/Turkish)
     - Currency selection (USD/EUR/TRY/GBP)
     - Expandable cards for selection
   - **Appearance Section:**
     - Three theme options are available:
       - System Default (follows device theme)
       - Light Mode (always light)
       - Dark Mode (always dark)
   - **Notifications Section:**
     - General notifications toggle
     - Price alerts toggle
   - **About Section:**
     - Version information
     - Terms of Service, Privacy Policy, Rate App links
3. User taps desired option
4. Changes apply immediately without app restart
5. Language change updates all UI texts instantly
6. If back button is tapped → Returns to portfolio screen

### 4.3 Coin Discovery and Purchase Flow
1. All coins are displayed on coin list screen
2. If coin is long pressed → Price chart is displayed
3. If coin is tapped → Goes to buy screen
4. On buy screen:
   - Coin information is displayed
   - Available balance is displayed
   - Purchase amount is entered
   - "Buy" button is tapped
5. After successful transaction, returns to portfolio screen

### 4.4 Coin Selling Flow
1. Coin is tapped on portfolio screen
2. On sell screen:
   - Coin information is displayed
   - Amount owned is displayed
   - Sell amount is entered
   - "Sell" button is tapped
3. After successful transaction:
   - Transaction is saved to database
   - Returns to portfolio screen

### 4.5 Transaction History Viewing Flow
1. "History" button is tapped on portfolio screen
2. On transaction history screen:
   - All transactions are listed in chronological order
   - Detailed information is displayed for each transaction
   - Transaction type is color-coded (BUY: green, SELL: red)
3. If back button is tapped → Returns to portfolio screen

### 4.6 Portfolio Analytics Viewing Flow
1. "Analytics" button is tapped on portfolio screen
2. On analytics screen:
   - Profit/loss summary section is displayed at top
   - Portfolio distribution pie chart is displayed
   - Portfolio history line chart is displayed (if sufficient data)
   - Transaction statistics are displayed
   - Individual coin performances are listed
3. If back button is tapped → Returns to portfolio screen

### 4.7 Price Alert Creation Flow
1. "Alerts" button is tapped on portfolio screen
2. On price alerts screen:
   - All alerts are listed
   - Alerts can be enabled/disabled via toggle
   - Alerts can be deleted
3. If "+" FAB is tapped → Goes to coin list screen
4. On coin list screen, if bell icon is tapped → Goes to create alert screen
5. On create alert screen:
   - Coin information and current price is displayed
   - Alert condition is selected (Above/Below)
   - Target price is entered
   - "Create Alert" button is tapped
6. After successful creation → Returns to price alerts screen

---

## 5. Technical Requirements

### 5.1 Minimum System Requirements
- **Android:**
  - Minimum SDK: 24 (Android 7.0 Nougat)
  - Target SDK: 35 (Android 15)
  - Biometric hardware support (optional)
  
- **iOS:**
  - iOS 13.0+
  - Face ID / Touch ID support (optional)

### 5.2 Dependencies
- Kotlin 2.0.21
- Compose Multiplatform 1.7.0
- Room 2.7.0-alpha11
- Ktor 3.0.0
- Koin 4.0.0
- Coil 3.0.0
- Kotlinx Serialization 1.7.3
- Kotlinx DateTime 0.6.1

### 5.3 Performance Requirements
- Timeout management for API calls
- Offline-first approach (local database)
- Reactive state management (Flow/StateFlow)
- Image caching (Coil)
- Efficient database queries

---

## 6. Error Handling and Edge Cases

### 6.1 Network Errors
- **REQUEST_TIMEOUT:** When request times out
- **NO_INTERNET:** When no internet connection
- **SERVER_ERROR:** Server error (500-599)
- **TOO_MANY_REQUESTS:** Rate limiting (429)
- **SERIALIZATION:** JSON parse error
- **UNKNOWN:** Unknown errors

### 6.2 Local Errors
- **INSUFFICIENT_FUNDS:** Insufficient balance or coin amount
- Database errors

### 6.3 Error Handling Strategy
- User-friendly error messages (`DataErrorToString`)
- Error state display in UI
- Retry mechanisms (can be added in future)
- Graceful degradation

---

## 7. Security Features

### 7.1 Biometric Authentication
- Platform-native biometric authentication
- Mandatory authentication at application startup
- Platform-specific implementations

### 7.2 Data Security
- Local data storage with Room database
- HTTPS usage for network requests
- Secure storage of sensitive data

---

## 8. UI/UX Features

### 8.1 Design System
- Material Design 3 usage
- Custom theme (`CointyTheme`)
- Custom color palette (`CointyColors`)
- Typography system (`Font`)
- Dark mode support with dedicated color schemes
- Dynamic theme switching (Light/Dark/System)

### 8.2 User Experience
- Loading states (skeleton screens or progress indicators)
- Error states (user-friendly messages)
- Empty states
- Smooth navigation transitions
- Currency formatting and visual transformations
- Color-coded performance indicators (green/red)
- Animated charts with smooth transitions

---

## 9. Test Strategy

### 9.1 Test Libraries
- Kotlin Test
- AssertK (assertion library)
- Turbine (Flow testing)
- Coroutines Test
- Compose UI Test

### 9.2 Test Coverage
- Unit tests (Use cases, ViewModels)
- Integration tests (Repository)
- UI tests (Compose screens)
- Flow-based testing

### 9.3 Existing Unit Tests

#### 9.3.1 PortfolioViewModelTest
**Location:** `commonTest/kotlin/com/kaaneneskpc/cointy/portfolio/presentation/PortfolioViewModelTest.kt`

**Test Scenarios:**
- `State and portfolio coins are properly combined` - Proper combination of state and portfolio coins
- `Portfolio value updates when a coin is added` - Portfolio value update when coin is added
- `Loading state and error message update on failure` - Loading state and error message update on failure

**Techniques Used:**
- Flow testing with Turbine
- Coroutine testing with UnconfinedTestDispatcher
- Dependency mocking with FakePortfolioRepository

#### 9.3.2 FakePortfolioRepository
**Location:** `commonTest/kotlin/com/kaaneneskpc/cointy/portfolio/data/FakePortfolioRepository.kt`

**Features:**
- Fake implementation of `PortfolioRepository` interface
- Reactive test data management with MutableStateFlow
- `simulateError()` method for testing error scenarios
- Companion object with test data (fakeCoin, portfolioCoin, cashBalance)

### 9.4 Test Structure
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

### 3.10 Settings and Personalization
**Purpose:** To allow users to customize their app experience with comprehensive settings including profile, theme, language, and notification preferences.

**Features:**
- **User Profile:**
  - Profile photo display (avatar with initials)
  - First name and last name fields
  - Edit profile dialog
  - Profile section with visual feedback

- **Language Selection (Multi-language Support):**
  - English language support
  - Turkish language support (Türkçe)
  - Real-time language switching without app restart
  - All UI texts change dynamically based on selected language
  - Localization system with StringResources

- **Theme Selection:**
  - System Default: Follow device theme settings
  - Light Mode: Always use light theme
  - Dark Mode: Always use dark theme
  - Real-time theme switching without app restart

- **Currency Selection:**
  - USD (US Dollar)
  - EUR (Euro)
  - TRY (Turkish Lira)
  - GBP (British Pound)
  - Expandable selection card

- **Notification Settings:**
  - General notifications toggle
  - Price alerts toggle (disabled when general notifications are off)
  - Visual feedback for toggle states

- **About Section:**
  - Version information
  - Terms of Service link
  - Privacy Policy link
  - Rate App link

- **Settings Screen:**
  - Accessible from Portfolio screen via settings icon
  - Clean and intuitive UI with sections
  - Scrollable content for all settings
  - Visual feedback for selected options

**Technical Details:**
- `ThemeMode` - Enum for theme options (LIGHT, DARK, SYSTEM)
- `Language` - Enum for language options (ENGLISH, TURKISH)
- `Currency` - Enum for currency options (USD, EUR, TRY, GBP)
- `UserProfile` - Data class for user profile (firstName, lastName, profilePhotoUri)
- `SettingsRepository` - Repository interface for all settings
- `SettingsDataSource` - Data source interface for settings preferences
- `InMemorySettingsDataSource` - In-memory implementation for settings storage
- `SettingsRepositoryImpl` - Repository implementation
- `SettingsViewModel` - State management with StateFlow
- `SettingsScreen` - Settings Compose UI with multiple sections
- `SettingsState` - UI state data class with all settings fields

**Localization System:**
- `StringResources` - Data class containing all UI strings
- `EnglishStrings` - English language strings
- `TurkishStrings` - Turkish language strings
- `LocalStringResources` - CompositionLocal for string access
- `ProvideStringResources` - Composable wrapper for language context
- `getStringResources()` - Function to get strings by language

**Theme Implementation:**
- `CointyTheme` - Updated to accept ThemeMode parameter
- Dynamic color scheme switching based on theme preference
- Support for system theme detection via `isSystemInDarkTheme()`
- Consistent color palette for both light and dark themes
- Custom profit/loss colors for both themes

**Settings Models:**
- `Language` - Enum with code and displayName:
  - ENGLISH: "en", "English"
  - TURKISH: "tr", "Türkçe"

- `Currency` - Enum with code, symbol, and displayName:
  - USD: "$", "US Dollar"
  - EUR: "€", "Euro"
  - TRY: "₺", "Turkish Lira"
  - GBP: "£", "British Pound"

- `UserProfile` - Data class:
  - firstName: User's first name
  - lastName: User's last name
  - profilePhotoUri: Optional profile photo URI

---

### 3.11 Price Alerts and Notifications
**Purpose:** For users to set price alerts on cryptocurrencies and receive notifications when target prices are reached.

**Features:**
- **Price Alert Management:**
  - Create price alerts for any cryptocurrency
  - Set target price with condition (Above/Below)
  - View all created alerts
  - Enable/disable alerts
  - Delete alerts
  - Track triggered alerts

- **Alert Creation:**
  - Select coin from coin list
  - Set target price
  - Choose condition (price goes above or below)
  - View current price while creating

- **Notifications:**
  - Platform-specific notifications (Android: NotificationCompat, iOS: UNUserNotificationCenter)
  - Notification permission handling
  - Alert triggered notifications with coin details

**Technical Details:**
- `PriceAlertEntity` - Database entity for alerts
- `PriceAlertDao` - Database Access Object
- `PriceAlertRepository` - Repository interface and implementation
- `CreatePriceAlertUseCase` - Create new alert
- `GetPriceAlertsUseCase` - Get all/active/by coin alerts
- `DeletePriceAlertUseCase` - Delete alert
- `TogglePriceAlertUseCase` - Enable/disable alert
- `CheckPriceAlertsUseCase` - Check and trigger alerts
- `PriceAlertViewModel` - State management with StateFlow
- `PriceAlertScreen` - Alert list Compose UI
- `CreateAlertScreen` - Alert creation Compose UI
- `NotificationService` - Platform abstraction for notifications
- `NotificationManager` - Permission and channel management

**Alert Models:**
- `PriceAlertModel` - Domain model containing:
  - id, coinId, coinName, coinSymbol, coinIconUrl
  - targetPrice: Target price for alert
  - condition: ABOVE or BELOW
  - isEnabled: Alert active status
  - isTriggered: Whether alert has been triggered
  - createdAt, triggeredAt: Timestamps

- `AlertCondition` - Enum for alert conditions:
  - ABOVE: Trigger when price goes above target
  - BELOW: Trigger when price goes below target

---

## 10. Future Enhancements

### 10.1 Suggested Features
- **Notifications:**
  - Portfolio value change notifications
  
- **Favorites:**
  - Add coins to favorites
  - Favorite coin list
  
- **Search and Filtering:**
  - Coin search feature
  - Filtering by price, change, and other criteria
  - Transaction history filtering (by coin, date, type)
  
- **Offline Mode:**
  - Work without internet
  - Work with cached data
  
- **Export/Import:**
  - Export portfolio data
  - Export transaction history (CSV, PDF)
  - Backup and restore features

- **Advanced Analytics:**
  - Time period selection (24h, 7d, 30d, 1y)
  - Comparison with market benchmarks
  - Risk analysis metrics
  - Investment recommendations

- **Persistent Settings Storage:**
  - Save all settings to local storage
  - Restore settings on app launch
  - Cloud sync for settings

- **Additional Languages:**
  - German, French, Spanish support
  - RTL language support (Arabic, Hebrew)

---

## 11. Project Structure

### 11.1 Module Organization
```
composeApp/src/
├── commonMain/
│   └── kotlin/com/kaaneneskpc/cointy/
│       ├── App.kt                    # Main application entry point
│       ├── alert/                    # Price alerts module
│       │   ├── data/                 # Data layer
│       │   │   ├── local/
│       │   │   │   ├── PriceAlertDao.kt
│       │   │   │   └── PriceAlertEntity.kt
│       │   │   ├── mapper/
│       │   │   │   └── PriceAlertMapper.kt
│       │   │   └── PriceAlertRepositoryImpl.kt
│       │   ├── domain/               # Domain layer
│       │   │   ├── model/
│       │   │   │   ├── AlertCondition.kt
│       │   │   │   └── PriceAlertModel.kt
│       │   │   ├── CheckPriceAlertsUseCase.kt
│       │   │   ├── CreatePriceAlertUseCase.kt
│       │   │   ├── DeletePriceAlertUseCase.kt
│       │   │   ├── GetPriceAlertsUseCase.kt
│       │   │   ├── PriceAlertRepository.kt
│       │   │   └── TogglePriceAlertUseCase.kt
│       │   └── presentation/         # Presentation layer
│       │       ├── component/
│       │       │   └── CreateAlertDialog.kt
│       │       ├── CreateAlertScreen.kt
│       │       ├── PriceAlertScreen.kt
│       │       ├── PriceAlertState.kt
│       │       └── PriceAlertViewModel.kt
│       ├── analytics/                # Portfolio analytics module
│       │   ├── data/                 # Data layer
│       │   │   └── AnalyticsRepositoryImpl.kt
│       │   ├── domain/               # Domain layer
│       │   │   ├── AnalyticsRepository.kt
│       │   │   ├── GetPortfolioAnalyticsUseCase.kt
│       │   │   └── model/
│       │   │       └── PortfolioAnalytics.kt
│       │   └── presentation/         # Presentation layer
│       │       ├── AnalyticsScreen.kt
│       │       ├── AnalyticsState.kt
│       │       ├── AnalyticsViewModel.kt
│       │       └── component/
│       │           ├── AnalyticsCard.kt
│       │           ├── PieChart.kt
│       │           └── PortfolioHistoryChart.kt
│       ├── biometric/                # Biometric authentication
│       ├── coins/                    # Coin discovery module
│       │   ├── data/                 # Data layer
│       │   ├── domain/               # Domain layer
│       │   └── presentation/         # Presentation layer
│       │       ├── component/
│       │       │   ├── CoinChart.kt          # Legacy sparkline chart
│       │       │   └── TradingViewChart.kt   # TradingView-style chart
│       │       ├── CoinListScreen.kt
│       │       ├── CoinListViewModel.kt
│       │       └── CoinState.kt
│       ├── core/                     # Core utilities and abstractions
│       │   ├── biometric/
│       │   ├── database/
│       │   ├── domain/
│       │   ├── navigation/
│       │   ├── network/
│       │   ├── notification/
│       │   │   ├── NotificationManager.kt
│       │   │   └── NotificationService.kt
│       │   └── util/
│       ├── di/                       # Dependency Injection (Koin)
│       ├── portfolio/                # Portfolio management module
│       │   ├── data/
│       │   ├── domain/
│       │   └── presentation/
│       ├── settings/                 # Settings and personalization module
│       │   ├── data/                 # Data layer
│       │   │   ├── InMemorySettingsDataSource.kt
│       │   │   ├── SettingsDataSource.kt
│       │   │   └── SettingsRepositoryImpl.kt
│       │   ├── domain/               # Domain layer
│       │   │   ├── model/
│       │   │   │   ├── Currency.kt
│       │   │   │   ├── Language.kt
│       │   │   │   ├── ThemeMode.kt
│       │   │   │   └── UserProfile.kt
│       │   │   └── SettingsRepository.kt
│       │   └── presentation/         # Presentation layer
│       │       ├── SettingsScreen.kt
│       │       ├── SettingsState.kt
│       │       └── SettingsViewModel.kt
│       ├── core/
│       │   ├── localization/         # Multi-language support
│       │   │   └── StringResources.kt
│       ├── theme/                    # UI theme and styles
│       ├── trade/                    # Buy/sell module
│       │   ├── domain/
│       │   ├── mapper/
│       │   └── presentation/
│       └── transaction/              # Transaction history module
│           ├── data/
│           │   ├── local/
│           │   └── mapper/
│           ├── domain/
│           └── presentation/
├── androidMain/                      # Android-specific code
└── iosMain/                          # iOS-specific code
```

### 11.2 Architectural Layers
- **Presentation Layer:** ViewModels, UI Components (Compose)
- **Domain Layer:** Use Cases, Models, Repository Interfaces
- **Data Layer:** Repository Implementations, Data Sources (Remote/Local), Mappers, DTOs

---

## 12. Version Information

- **Version Code:** 1
- **Version Name:** 1.0
- **Database Version:** 4
- **Kotlin Version:** 2.0.21
- **Compose Multiplatform:** 1.7.0

---

## 13. Notes and Important Decisions

### 13.1 Architectural Decisions
- Compliance with Clean Architecture principles
- MVVM pattern usage
- Data abstraction with Repository pattern
- Business logic separation with Use Case pattern

### 13.2 Technology Choices
- **Kotlin Multiplatform:** For code sharing
- **Compose Multiplatform:** Modern, declarative UI
- **Room Database:** Reliable, performant local data storage
- **Ktor:** Modern, coroutine-based network library
- **Koin:** Lightweight, easy-to-use DI framework

### 13.3 API Integration
- CoinRanking API usage
- RESTful API pattern
- JSON serialization (Kotlinx Serialization)
- Error handling and retry mechanisms

---

## 14. Documentation and Resources

### 14.1 Related Documentation
- Kotlin Multiplatform: https://kotlinlang.org/docs/multiplatform.html
- Compose Multiplatform: https://www.jetbrains.com/lp/compose-multiplatform/
- Room Database: https://developer.android.com/training/data-storage/room
- Ktor: https://ktor.io/
- CoinRanking API: https://developers.coinranking.com/

### 14.2 In-Project Documentation
- README.md - Project setup and build instructions
- This PRD documentation

---

**Last Updated:** 2025
**Documentation Version:** 1.3
