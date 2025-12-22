import WidgetKit
import SwiftUI

struct CoinData: Identifiable {
    let id: String
    let symbol: String
    let name: String
    let price: Double
    let change24h: Double
}

struct CoinPriceEntry: TimelineEntry {
    let date: Date
    let coins: [CoinData]
}

struct CoinPriceProvider: TimelineProvider {
    func placeholder(in context: Context) -> CoinPriceEntry {
        CoinPriceEntry(date: Date(), coins: [
            CoinData(id: "1", symbol: "BTC", name: "Bitcoin", price: 43250.0, change24h: 2.5),
            CoinData(id: "2", symbol: "ETH", name: "Ethereum", price: 2280.0, change24h: -1.2)
        ])
    }
    
    func getSnapshot(in context: Context, completion: @escaping (CoinPriceEntry) -> Void) {
        let entry = loadCoinData()
        completion(entry)
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<CoinPriceEntry>) -> Void) {
        let entry = loadCoinData()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
    
    private func loadCoinData() -> CoinPriceEntry {
        let sharedDefaults = UserDefaults(suiteName: "group.com.kaaneneskpc.cointy")
        var coins: [CoinData] = []
        if let coinsData = sharedDefaults?.data(forKey: "widgetCoins"),
           let decoded = try? JSONDecoder().decode([CoinDataCodable].self, from: coinsData) {
            coins = decoded.map { CoinData(id: $0.id, symbol: $0.symbol, name: $0.name, price: $0.price, change24h: $0.change24h) }
        }
        return CoinPriceEntry(date: Date(), coins: Array(coins.prefix(5)))
    }
}

struct CoinDataCodable: Codable {
    let id: String
    let symbol: String
    let name: String
    let price: Double
    let change24h: Double
}

struct CoinPriceWidgetEntryView: View {
    var entry: CoinPriceProvider.Entry
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text("Coin Prices")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.white)
                Spacer()
            }
            .padding(.bottom, 4)
            
            if entry.coins.isEmpty {
                Spacer()
                HStack {
                    Spacer()
                    Text("No coins in portfolio")
                        .font(.system(size: 12))
                        .foregroundColor(Color(hex: "8E8E93"))
                    Spacer()
                }
                Spacer()
            } else {
                ForEach(entry.coins) { coin in
                    CoinRowView(coin: coin)
                }
                Spacer()
            }
        }
        .padding(12)
        .containerBackground(Color(hex: "1C1C1E"), for: .widget)
    }
}

struct CoinRowView: View {
    let coin: CoinData
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 1) {
                Text(coin.symbol)
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(.white)
                Text(coin.name.prefix(10))
                    .font(.system(size: 9))
                    .foregroundColor(Color(hex: "8E8E93"))
            }
            .frame(width: 60, alignment: .leading)
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 1) {
                Text(formatCurrency(coin.price))
                    .font(.system(size: 11, weight: .medium))
                    .foregroundColor(.white)
                Text(formatChange(coin.change24h))
                    .font(.system(size: 9))
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
        return String(format: "%@%.2f%%", sign, change)
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
        .supportedFamilies([.systemMedium, .systemLarge])
    }
}

#Preview(as: .systemMedium) {
    CoinPriceWidget()
} timeline: {
    CoinPriceEntry(date: .now, coins: [
        CoinData(id: "1", symbol: "BTC", name: "Bitcoin", price: 43250.0, change24h: 2.5),
        CoinData(id: "2", symbol: "ETH", name: "Ethereum", price: 2280.0, change24h: -1.2),
        CoinData(id: "3", symbol: "SOL", name: "Solana", price: 98.50, change24h: 5.3)
    ])
}
