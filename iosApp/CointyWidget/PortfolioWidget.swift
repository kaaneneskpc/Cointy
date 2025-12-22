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
        PortfolioEntry(date: Date(), totalValue: 10000.0, cashBalance: 10000.0, lastUpdated: Date(), debugInfo: "")
    }
    
    func getSnapshot(in context: Context, completion: @escaping (PortfolioEntry) -> Void) {
        let entry = loadPortfolioData()
        completion(entry)
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<PortfolioEntry>) -> Void) {
        let entry = loadPortfolioData()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 15, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
    
    private func loadPortfolioData() -> PortfolioEntry {
        var debugInfo = ""
        var totalValue: Double = 0.0
        var cashBalance: Double = 10000.0
        var timestamp: Double = 0
        
        if let sharedDefaults = UserDefaults(suiteName: "group.com.kaaneneskpc.cointy") {
            totalValue = sharedDefaults.double(forKey: "totalPortfolioValue")
            let storedCash = sharedDefaults.double(forKey: "cashBalance")
            if storedCash > 0 {
                cashBalance = storedCash
            }
            timestamp = sharedDefaults.double(forKey: "lastUpdatedTimestamp")
            debugInfo = totalValue > 0 ? "✓" : "○"
        } else {
            debugInfo = "✗"
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
        GeometryReader { geo in
            ZStack {
                WidgetColors.background
                
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text("💰 Cointy")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.white)
                        Text(entry.debugInfo)
                            .font(.system(size: 8))
                            .foregroundColor(WidgetColors.debugGray)
                        Spacer()
                    }
                    
                    Spacer()
                    
                    Text("Total Value")
                        .font(.system(size: 9))
                        .foregroundColor(WidgetColors.textGray)
                    
                    Text(formatCurrency(entry.totalValue))
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(entry.totalValue > 0 ? WidgetColors.profitGreen : WidgetColors.textGray)
                        .minimumScaleFactor(0.6)
                        .lineLimit(1)
                    
                    Spacer()
                    
                    HStack {
                        VStack(alignment: .leading, spacing: 1) {
                            Text("Cash")
                                .font(.system(size: 7))
                                .foregroundColor(WidgetColors.textGray)
                            Text(formatCurrency(entry.cashBalance))
                                .font(.system(size: 10, weight: .medium))
                                .foregroundColor(.white)
                        }
                        Spacer()
                        Text(formatTime(entry.lastUpdated))
                            .font(.system(size: 7))
                            .foregroundColor(WidgetColors.textGray)
                    }
                }
                .padding(10)
            }
        }
    }
    
    private func formatCurrency(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        formatter.maximumFractionDigits = amount >= 1000 ? 0 : 2
        return formatter.string(from: NSNumber(value: amount)) ?? "$0.00"
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
    }
}

#Preview(as: .systemSmall) {
    PortfolioWidget()
} timeline: {
    PortfolioEntry(date: .now, totalValue: 15234.56, cashBalance: 5000.0, lastUpdated: .now, debugInfo: "✓")
}
