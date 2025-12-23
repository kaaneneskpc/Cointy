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

### 3.15 Risk Analysis
**Purpose:** To calculate portfolio volatility and provide users with risk scoring to help them understand their investment risk exposure.

**Features:**
- **Risk Score Display:**
  - Animated circular gauge showing risk score (0-100)
  - Color-coded risk levels (green to red gradient)
  - Risk level label (Low, Moderate, High, Critical)

- **Risk Breakdown:**
  - Portfolio volatility percentage
  - Diversification score (based on number of coins)
  - Concentration risk (max allocation percentage)

- **Coin Risk Metrics:**
  - Individual coin volatility (24h price change)
  - Allocation percentage per coin
  - Risk contribution to overall portfolio
  - Sorted by risk contribution

- **Risk Levels:**
  - LOW (0-30): Conservative portfolio with stable assets
  - MODERATE (31-50): Balanced risk exposure
  - HIGH (51-70): Elevated volatility
  - CRITICAL (71-100): Very high-risk portfolio

- **Empty State:**
  - User-friendly message when no portfolio data exists
  - Guidance to start investing

**Technical Details:**
- `RiskAnalysisRepository` - Repository interface for risk analysis
- `RiskAnalysisRepositoryImpl` - Implementation with volatility calculations
- `GetPortfolioRiskAnalysisUseCase` - Use case for fetching risk data
- `RiskAnalysisViewModel` - State management with StateFlow
- `RiskAnalysisScreen` - Main risk analysis Compose UI
- `RiskAnalysisState` - UI state data class

**Risk Calculation Algorithm:**
- **Volatility Calculation:** Weighted average of 24h price changes based on portfolio allocation
  - Formula: Σ(coin_allocation_% × |coin_24h_change_%|)
- **Risk Score Formula:** Weighted combination of three factors
  - Volatility Component (50% weight): Normalized portfolio volatility
  - Diversification Component (30% weight): Inverse of coin count (max 10)
  - Concentration Component (20% weight): Maximum allocation percentage
- **Risk Score:** (Volatility × 0.5) + ((100 - Diversification) × 0.3) + (Concentration × 0.2)

**Risk Models:**
- `RiskLevel` - Enum (LOW, MODERATE, HIGH, CRITICAL)
- `PortfolioRiskAnalysis` - Comprehensive risk data model containing:
  - portfolioVolatility: Weighted average volatility
  - riskScore: Calculated risk score (0-100)
  - riskLevel: Categorized risk level enum
  - diversificationScore: Based on coin count
  - concentrationRisk: Maximum allocation percentage
  - coinRiskMetrics: List of per-coin risk data

- `CoinRiskMetrics` - Individual coin risk data:
  - coinId, coinName, coinSymbol, coinIconUrl
  - volatility: Absolute 24h price change
  - allocationPercentage: Portfolio weight
  - contributionToRisk: Risk contribution to portfolio

**UI Components:**
- `RiskScoreGauge` - Animated circular gauge with color gradient
- `CoinRiskItem` - Individual coin risk row with volatility indicator
- `RiskAnalysisButton` - Navigation card on Analytics screen

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

### 3.11 Export Portfolio Data
**Purpose:** For users to export their portfolio data and transaction history for backup, analysis, or record-keeping purposes.

**Features:**
- **Export Formats:**
  - CSV format for spreadsheet compatibility
  - JSON format for data interchange and backup

- **Export Content:**
  - Portfolio holdings (coin details, amounts, purchase prices, performance)
  - Transaction history (all buy/sell transactions)
  - Cash balance and total portfolio value
  - Export timestamp

- **Export Preview:**
  - View summary before exporting
  - Portfolio coin count
  - Transaction count
  - Total portfolio value
  - Cash balance

- **Platform-Specific Export:**
  - Android: Saves to Downloads folder via MediaStore API
  - iOS: Saves to Documents folder (accessible via Files app)

- **Export Screen:**
  - Format selection (CSV/JSON)
  - Export preview with statistics
  - Export button with loading state
  - Success/error feedback with file name

**Technical Details:**
- `ExportFormat` - Enum for export formats (CSV, JSON)
- `ExportData` - Data class containing all export data
- `ExportResult` - Sealed interface for Success/Error results
- `ExportRepository` - Repository interface for export operations
- `ExportRepositoryImpl` - Implementation with CSV/JSON generation
- `ExportPortfolioDataUseCase` - Use case for executing export
- `GetExportDataUseCase` - Use case for fetching export preview data
- `ExportViewModel` - State management with StateFlow
- `ExportScreen` - Export Compose UI
- `FileExporter` - Platform abstraction for file operations
- `AndroidFileExporter` - Android implementation (MediaStore)
- `IosFileExporter` - iOS implementation (NSFileManager)

**Export Data Models:**
- `PortfolioExportItem` - Portfolio coin export data:
  - coinId, coinName, coinSymbol
  - averagePurchasePrice, ownedAmountInUnit, ownedAmountInFiat
  - performancePercent

- `TransactionExportItem` - Transaction export data:
  - id, type, coinId, coinName, coinSymbol
  - amountInFiat, amountInUnit, price
  - timestamp, formattedDate

**CSV Format:**
- Header section with export date, total value, cash balance
- Portfolio holdings section with column headers
- Transaction history section with column headers

**JSON Format:**
- Structured JSON with exportDate, summary, portfolioHoldings, transactions
- Pretty-printed for readability
- Includes all portfolio and transaction details

---

### 3.12 Price Alerts and Notifications
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

- **Background Price Checking:**
  - Automatic price checking even when app is closed
  - Android: WorkManager with 15-minute periodic work
  - iOS: BGTaskScheduler with BGAppRefreshTask
  - Network connectivity constraint for efficient battery usage
  - Automatic retry on failure with exponential backoff

**Technical Details:**
- `PriceAlertEntity` - Database entity for alerts
- `PriceAlertDao` - Database Access Object
- `PriceAlertRepository` - Repository interface and implementation
- `CreatePriceAlertUseCase` - Create new alert
- `GetPriceAlertsUseCase` - Get all/active/by coin alerts
- `DeletePriceAlertUseCase` - Delete alert
- `TogglePriceAlertUseCase` - Enable/disable alert
- `CheckPriceAlertsUseCase` - Check and trigger alerts
- `BackgroundCheckPriceAlertsUseCase` - Background price fetching and alert checking
- `PriceAlertViewModel` - State management with StateFlow
- `PriceAlertScreen` - Alert list Compose UI
- `CreateAlertScreen` - Alert creation Compose UI
- `NotificationService` - Platform abstraction for notifications
- `NotificationManager` - Permission and channel management
- `BackgroundPriceChecker` - Common interface for background tasks
- `PriceAlertWorker` - Android WorkManager worker
- `AndroidBackgroundPriceChecker` - Android WorkManager implementation
- `IosBackgroundPriceChecker` - iOS BGTaskScheduler implementation
- `IosBackgroundTaskHandler` - iOS background task registration and handling

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

### 3.16 Volatility Notifications
**Purpose:** To automatically notify users when cryptocurrencies experience significant price changes (e.g., "Bitcoin dropped 5%") without requiring manual alert setup.

**Features:**
- **Automatic Price Change Detection:**
  - Monitor 24h price change of all coins
  - Configurable threshold (3%, 5%, 10%, 15%)
  - Automatic notification when threshold is exceeded

- **User Settings:**
  - Enable/disable volatility notifications
  - Select price change threshold percentage
  - Integrated with general notification settings

- **Spam Prevention:**
  - 1-hour cooldown per coin after notification
  - Prevents duplicate notifications for same coin
  - Respects general notification settings

- **Localization:**
  - English and Turkish support
  - Dynamic notification text with coin name and change percentage

**Technical Details:**
- `VolatilityNotificationModel` - Data model for volatility alerts
- `CheckVolatilityAlertsUseCase` - Use case to check and send volatility notifications
- `VolatilityNotificationTracker` - In-memory tracker for cooldown management
- `NotificationService.showVolatilityNotification()` - Platform notification method
- Integration with `BackgroundCheckPriceAlertsUseCase` for automatic checking

**Settings Integration:**
- `volatilityAlertsEnabled` - Boolean preference stored in DataStore
- `volatilityThreshold` - Double preference (default: 5.0%)
- Settings UI with toggle and threshold selector

**Notification Format:**
- Title: "{CoinSymbol} Price Alert"
- Message: "{CoinName} dropped/rose X% in the last 24h. Current price: $X"

---

### 3.13 Interactive Onboarding
**Purpose:** To introduce the app's core concepts (Virtual Balance, Portfolio Management) and provide a smooth first-time user experience.

**Features:**
- **Interactive Pager:** Smooth horizontal paging between onboarding steps.
- **Animated Icons:** Floating, pulsing, and wobbling animations for icons to create a lively feel.
- **Localized Content:** Fully translated strings for both English and Turkish.
- **Progress Indicators:** Visual feedback for the current page.
- **Navigation Control:** Skip or proceed to the end to get started.

**Technical Details:**
- `OnboardingViewModel` - State management and logic for page transitions.
- `OnboardingScreen` - Compose UI with `HorizontalPager` and custom animations.
- `InfiniteTransition` - Used for continuous, smooth icon animations.
- `SettingsRepository` - Persists the onboarding completion state.
- `OnboardingState` - UI state model.

---

### 3.14 Home Screen Widgets (Android)
**Purpose:** To provide users with quick access to portfolio information directly from their home screen without opening the app.

**Features:**
- **Portfolio Value Widget:**
  - Display total portfolio value
  - Show cash balance
  - Last updated time indicator
  - Manual refresh button
  - Tap to open app

- **Coin Prices Widget:**
  - List of portfolio coins (up to 5)
  - Current price for each coin
  - 24h price change percentage with color coding
  - Manual refresh button
  - Empty state when no coins in portfolio
  - Tap to open app

- **Widget Customization:**
  - Resizable widgets (horizontal and vertical)
  - Dark theme design matching app theme
  - Automatic periodic updates (30 minutes)

**Technical Details:**
- `PortfolioWidget` - Glance widget for portfolio summary
- `CoinPriceWidget` - Glance widget for coin prices
- `PortfolioWidgetReceiver` - Widget receiver for system registration
- `CoinPriceWidgetReceiver` - Widget receiver for coin prices
- `GetWidgetDataUseCase` - Use case for fetching widget data
- `WidgetData` - Data models for widget display
- `RefreshPortfolioWidgetAction` - Action callback for refresh
- `RefreshCoinPriceWidgetAction` - Action callback for refresh

**Widget Data Models:**
- `PortfolioWidgetData` - Portfolio summary:
  - totalPortfolioValue: Total value of all holdings
  - cashBalance: Available cash balance
  - lastUpdated: Timestamp of last data refresh

- `CoinWidgetData` - Individual coin data:
  - coinId, name, symbol, iconUrl
  - price: Current price
  - change24h: 24-hour price change percentage

**Android-Specific Implementation:**
- Jetpack Glance 1.1.0 for Compose-based widgets
- Material 3 design language
- Koin dependency injection integration
- WorkManager for background updates

---

## 4. User Flows

### 4.1 Application Startup Flow
1. Application opens.
2. If onboarding not completed → Goes to Onboarding flow.
3. If onboarding completed → Goes to Biometric Authentication screen.
4. User completes authentication.
5. Redirected to portfolio screen.

### 4.9 Onboarding Flow
1. Application opens for the first time.
2. Onboarding screen is displayed.
3. User swiped or taps "Next" to navigate through:
   - Welcome to Cointy.
   - Virtual Balance explanation.
   - Portfolio Management explanation.
4. User taps "Get Started" or "Skip".
5. Onboarding state is saved as completed in preferences.
6. Redirected to Biometric Authentication screen.

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
7. If "Export" button is tapped → Goes to export screen
8. If settings icon is tapped → Goes to settings screen

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

### 4.7 Export Portfolio Data Flow
1. "Export" button is tapped on portfolio screen
2. On export screen:
   - Export preview is displayed (coin count, transaction count, values)
   - Export format selection (CSV/JSON)
3. User selects desired format
4. "Export" button is tapped
5. File is saved to:
   - Android: Downloads folder
   - iOS: Documents folder (accessible via Files app)
6. Success message is displayed with file name
7. If back button is tapped → Returns to portfolio screen

### 4.8 Price Alert Creation Flow
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
- Interactive and animated onboarding introduction

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

---

## 11. Project Structure

### 11.1 Module Organization
```
composeApp/src/
├── commonMain/
│   └── kotlin/com/kaaneneskpc/cointy/
│       ├── App.kt                    # Main application entry point
│       ├── alert/                    # Price alerts module
│       ├── analytics/                # Portfolio analytics module
│       ├── biometric/                # Biometric authentication
│       ├── onboarding/               # Interactive onboarding module
│       │   ├── presentation/         # Presentation layer
│       │   │   ├── OnboardingScreen.kt
│       │   │   ├── OnboardingState.kt
│       │   │   └── OnboardingViewModel.kt
│       ├── coins/                    # Coin discovery module
│       ├── export/                   # Export portfolio data module
│       ├── core/                     # Core utilities and abstractions
│       ├── di/                       # Dependency Injection (Koin)
│       ├── portfolio/                # Portfolio management module
│       ├── risk/                     # Risk analysis module
│       ├── settings/                 # Settings and personalization module
│       ├── theme/                    # UI theme and styles
│       ├── trade/                    # Buy/sell module
│       └── transaction/              # Transaction history module
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

---

**Last Updated:** December 2025
**Documentation Version:** 1.5
