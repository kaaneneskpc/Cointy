import WidgetKit
import SwiftUI

struct PortfolioEntry: TimelineEntry {
    let date: Date
    let totalValue: Double
    let cashBalance: Double
    let lastUpdated: Date
    let debugInfo: String
}

struct PortfolioProvider: TimelineProvider {
    func placeholder(in context: Context) -> PortfolioEntry {
        PortfolioEntry(date: Date(), totalValue: 10000.0, cashBalance: 10000.0, lastUpdated: Date(), debugInfo: "Placeholder")
    }
    
    func getSnapshot(in context: Context, completion: @escaping (PortfolioEntry) -> Void) {
        if context.isPreview {
            let entry = PortfolioEntry(date: Date(), totalValue: 15234.56, cashBalance: 5000.0, lastUpdated: Date(), debugInfo: "Preview")
            completion(entry)
        } else {
            let entry = loadPortfolioData()
            completion(entry)
        }
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<PortfolioEntry>) -> Void) {
        let entry = loadPortfolioData()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 15, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
    
    private func loadPortfolioData() -> PortfolioEntry {
        // Try App Group first
        var debugInfo = ""
        var totalValue: Double = 0.0
        var cashBalance: Double = 10000.0
        var timestamp: Double = 0
        
        if let sharedDefaults = UserDefaults(suiteName: "group.com.kaaneneskpc.cointy") {
            totalValue = sharedDefaults.double(forKey: "totalPortfolioValue")
            cashBalance = sharedDefaults.double(forKey: "cashBalance")
            timestamp = sharedDefaults.double(forKey: "lastUpdatedTimestamp")
            
            if totalValue > 0 || cashBalance != 10000.0 {
                debugInfo = "AppGroup ✓"
            } else {
                debugInfo = "AppGroup (empty)"
            }
        } else {
            debugInfo = "No AppGroup"
        }
        
        // If no data, use defaults
        if cashBalance == 0 {
            cashBalance = 10000.0
        }
        
        let lastUpdated = timestamp > 0 ? Date(timeIntervalSince1970: timestamp / 1000) : Date()
        
        return PortfolioEntry(
            date: Date(),
            totalValue: totalValue,
            cashBalance: cashBalance,
            lastUpdated: lastUpdated,
            debugInfo: debugInfo
        )
    }
}

struct PortfolioWidgetEntryView: View {
    @Environment(\.widgetFamily) var family
    var entry: PortfolioProvider.Entry
    
    var body: some View {
        ZStack {
            Color(hex: "1C1C1E")
            
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text("💰 Cointy")
                        .font(.system(size: family == .systemSmall ? 11 : 13, weight: .bold))
                        .foregroundColor(.white)
                    Spacer()
                    Button(intent: RefreshPortfolioIntent()) {
                        Image(systemName: "arrow.clockwise")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(Color(hex: "30D158"))
                    }
                    .buttonStyle(.plain)
                }
                
                Spacer()
                
                Text("Total Value")
                    .font(.system(size: family == .systemSmall ? 8 : 10))
                    .foregroundColor(Color(hex: "8E8E93"))
                
                Text(formatCurrency(entry.totalValue))
                    .font(.system(size: family == .systemSmall ? 18 : 24, weight: .bold))
                    .foregroundColor(entry.totalValue > 0 ? Color(hex: "30D158") : Color(hex: "8E8E93"))
                    .minimumScaleFactor(0.6)
                    .lineLimit(1)
                
                Spacer()
                
                HStack {
                    VStack(alignment: .leading, spacing: 1) {
                        Text("Cash")
                            .font(.system(size: 7))
                            .foregroundColor(Color(hex: "8E8E93"))
                        Text(formatCurrency(entry.cashBalance))
                            .font(.system(size: family == .systemSmall ? 9 : 11, weight: .medium))
                            .foregroundColor(.white)
                            .minimumScaleFactor(0.7)
                            .lineLimit(1)
                    }
                    Spacer()
                    VStack(alignment: .trailing, spacing: 1) {
                        Text(entry.debugInfo)
                            .font(.system(size: 6))
                            .foregroundColor(Color(hex: "666666"))
                        Text(formatTime(entry.lastUpdated))
                            .font(.system(size: 7))
                            .foregroundColor(Color(hex: "8E8E93"))
                    }
                }
            }
            .padding(10)
        }
        .containerBackground(.clear, for: .widget)
    }
    
    private func formatCurrency(_ amount: Double) -> String {
        if amount == 0 {
            return "$0.00"
        }
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        formatter.maximumFractionDigits = amount >= 1000 ? 0 : 2
        return formatter.string(from: NSNumber(value: amount)) ?? "$0"
    }
    
    private func formatTime(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: date)
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
        .contentMarginsDisabled()
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
    PortfolioEntry(date: .now, totalValue: 15234.56, cashBalance: 5000.0, lastUpdated: .now, debugInfo: "Preview")
}
