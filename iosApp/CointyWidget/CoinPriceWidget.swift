import WidgetKit
import SwiftUI

struct CoinData: Identifiable, Codable {
    let id: String
    let symbol: String
    let name: String
    let price: Double
    let change24h: Double
}

struct CoinPriceEntry: TimelineEntry {
    let date: Date
    let coins: [CoinData]
    let debugInfo: String
}

struct CoinPriceProvider: TimelineProvider {
    func placeholder(in context: Context) -> CoinPriceEntry {
        CoinPriceEntry(date: Date(), coins: [
            CoinData(id: "1", symbol: "BTC", name: "Bitcoin", price: 43250.0, change24h: 2.5),
            CoinData(id: "2", symbol: "ETH", name: "Ethereum", price: 2280.0, change24h: -1.2)
        ], debugInfo: "Placeholder")
    }
    
    func getSnapshot(in context: Context, completion: @escaping (CoinPriceEntry) -> Void) {
        if context.isPreview {
            let entry = placeholder(in: context)
            completion(entry)
        } else {
            let entry = loadCoinData()
            completion(entry)
        }
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<CoinPriceEntry>) -> Void) {
        let entry = loadCoinData()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 15, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
    
    private func loadCoinData() -> CoinPriceEntry {
        var coins: [CoinData] = []
        var debugInfo = ""
        
        if let sharedDefaults = UserDefaults(suiteName: "group.com.kaaneneskpc.cointy") {
            if let jsonString = sharedDefaults.string(forKey: "widgetCoins"),
               let jsonData = jsonString.data(using: .utf8) {
                do {
                    let decoded = try JSONDecoder().decode([CoinData].self, from: jsonData)
                    coins = decoded
                    debugInfo = "AppGroup ✓ (\(coins.count))"
                } catch {
                    debugInfo = "JSON Error"
                }
            } else {
                debugInfo = "AppGroup (no data)"
            }
        } else {
            debugInfo = "No AppGroup"
        }
        
        return CoinPriceEntry(date: Date(), coins: Array(coins.prefix(5)), debugInfo: debugInfo)
    }
}

struct CoinPriceWidgetEntryView: View {
    @Environment(\.widgetFamily) var family
    var entry: CoinPriceProvider.Entry
    
    var body: some View {
        ZStack {
            Color(hex: "1C1C1E")
            
            VStack(alignment: .leading, spacing: 2) {
                HStack {
                    Text("📈 Prices")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.white)
                    Text(entry.debugInfo)
                        .font(.system(size: 7))
                        .foregroundColor(Color(hex: "666666"))
                    Spacer()
                    Button(intent: RefreshCoinPriceIntent()) {
                        Image(systemName: "arrow.clockwise")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(Color(hex: "30D158"))
                    }
                    .buttonStyle(.plain)
                }
                .padding(.bottom, 2)
                
                if entry.coins.isEmpty {
                    Spacer()
                    HStack {
                        Spacer()
                        VStack(spacing: 4) {
                            Image(systemName: "wallet.pass")
                                .font(.system(size: 20))
                                .foregroundColor(Color(hex: "8E8E93"))
                            Text("Open app to sync")
                                .font(.system(size: 10))
                                .foregroundColor(Color(hex: "8E8E93"))
                        }
                        Spacer()
                    }
                    Spacer()
                } else {
                    ForEach(entry.coins.prefix(getMaxCoins())) { coin in
                        CoinRowView(coin: coin)
                    }
                    Spacer()
                }
            }
            .padding(10)
        }
        .containerBackground(.clear, for: .widget)
    }
    
    private func getMaxCoins() -> Int {
        switch family {
        case .systemSmall: return 2
        case .systemMedium: return 3
        case .systemLarge: return 6
        default: return 3
        }
    }
}

struct CoinRowView: View {
    let coin: CoinData
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 0) {
                Text(coin.symbol)
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(.white)
                Text(String(coin.name.prefix(8)))
                    .font(.system(size: 8))
                    .foregroundColor(Color(hex: "8E8E93"))
            }
            .frame(width: 50, alignment: .leading)
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 0) {
                Text(formatCurrency(coin.price))
                    .font(.system(size: 10, weight: .medium))
                    .foregroundColor(.white)
                    .minimumScaleFactor(0.7)
                    .lineLimit(1)
                Text(formatChange(coin.change24h))
                    .font(.system(size: 8, weight: .medium))
                    .foregroundColor(coin.change24h >= 0 ? Color(hex: "30D158") : Color(hex: "FF453A"))
            }
        }
        .padding(.vertical, 2)
    }
    
    private func formatCurrency(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        formatter.maximumFractionDigits = amount >= 100 ? 0 : 2
        return formatter.string(from: NSNumber(value: amount)) ?? "$0"
    }
    
    private func formatChange(_ change: Double) -> String {
        let sign = change >= 0 ? "+" : ""
        return String(format: "%@%.1f%%", sign, change)
    }
}

struct CoinPriceWidget: Widget {
    let kind: String = "CoinPriceWidget"
    
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: CoinPriceProvider()) { entry in
            CoinPriceWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Coin Prices")
        .description("Shows current prices of your portfolio coins")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
        .contentMarginsDisabled()
    }
}

#Preview(as: .systemMedium) {
    CoinPriceWidget()
} timeline: {
    CoinPriceEntry(date: .now, coins: [
        CoinData(id: "1", symbol: "BTC", name: "Bitcoin", price: 43250.0, change24h: 2.5),
        CoinData(id: "2", symbol: "ETH", name: "Ethereum", price: 2280.0, change24h: -1.2),
        CoinData(id: "3", symbol: "SOL", name: "Solana", price: 98.50, change24h: 5.3)
    ], debugInfo: "Preview")
}
