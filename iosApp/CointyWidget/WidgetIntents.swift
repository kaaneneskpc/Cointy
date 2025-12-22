import AppIntents
import WidgetKit

struct RefreshPortfolioIntent: AppIntent {
    static var title: LocalizedStringResource = "Refresh Portfolio"
    static var description = IntentDescription("Refreshes the portfolio widget data")
    
    func perform() async throws -> some IntentResult {
        WidgetCenter.shared.reloadTimelines(ofKind: "PortfolioWidget")
        return .result()
    }
}

struct RefreshCoinPriceIntent: AppIntent {
    static var title: LocalizedStringResource = "Refresh Coin Prices"
    static var description = IntentDescription("Refreshes the coin price widget data")
    
    func perform() async throws -> some IntentResult {
        WidgetCenter.shared.reloadTimelines(ofKind: "CoinPriceWidget")
        return .result()
    }
}
