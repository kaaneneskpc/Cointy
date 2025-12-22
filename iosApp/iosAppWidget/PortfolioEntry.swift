import WidgetKit
import SwiftUI

struct PortfolioEntry: TimelineEntry {
    let date: Date
    let totalValue: Double
    let cashBalance: Double
    let lastUpdated: Date
}

struct PortfolioProvider: TimelineProvider {
    func placeholder(in context: Context) -> PortfolioEntry {
        PortfolioEntry(date: Date(), totalValue: 10000.0, cashBalance: 10000.0, lastUpdated: Date())
    }
    
    func getSnapshot(in context: Context, completion: @escaping (PortfolioEntry) -> Void) {
        let entry = loadPortfolioData()
        completion(entry)
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<PortfolioEntry>) -> Void) {
        let entry = loadPortfolioData()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
    
    private func loadPortfolioData() -> PortfolioEntry {
        let sharedDefaults = UserDefaults(suiteName: "group.com.kaaneneskpc.cointy")
        let totalValue = sharedDefaults?.double(forKey: "totalPortfolioValue") ?? 0.0
        let cashBalance = sharedDefaults?.double(forKey: "cashBalance") ?? 10000.0
        let lastUpdated = sharedDefaults?.object(forKey: "lastUpdated") as? Date ?? Date()
        return PortfolioEntry(date: Date(), totalValue: totalValue, cashBalance: cashBalance, lastUpdated: lastUpdated)
    }
}

struct PortfolioWidgetEntryView: View {
    var entry: PortfolioProvider.Entry
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("Cointy Portfolio")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.white)
                Spacer()
            }
            
            Text("Total Value")
                .font(.system(size: 10))
                .foregroundColor(Color(hex: "8E8E93"))
            
            Text(formatCurrency(entry.totalValue))
                .font(.system(size: 24, weight: .bold))
                .foregroundColor(Color(hex: "30D158"))
            
            Spacer()
            
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Cash Balance")
                        .font(.system(size: 9))
                        .foregroundColor(Color(hex: "8E8E93"))
                    Text(formatCurrency(entry.cashBalance))
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.white)
                }
                Spacer()
                Text(formatTime(entry.lastUpdated))
                    .font(.system(size: 9))
                    .foregroundColor(Color(hex: "8E8E93"))
            }
        }
        .padding(16)
        .containerBackground(Color(hex: "1C1C1E"), for: .widget)
    }
    
    private func formatCurrency(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        return formatter.string(from: NSNumber(value: amount)) ?? "$0.00"
    }
    
    private func formatTime(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return "Updated: \(formatter.string(from: date))"
    }
}

struct PortfolioWidget: Widget {
    let kind: String = "PortfolioWidget"
    
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: PortfolioProvider()) { entry in
            PortfolioWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Portfolio Value")
        .description("Shows your total portfolio value and cash balance")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue:  Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

#Preview(as: .systemSmall) {
    PortfolioWidget()
} timeline: {
    PortfolioEntry(date: .now, totalValue: 15234.56, cashBalance: 5000.0, lastUpdated: .now)
}
