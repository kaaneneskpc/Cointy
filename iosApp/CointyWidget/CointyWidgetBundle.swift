import WidgetKit
import SwiftUI

@main
struct CointyWidgetBundle: WidgetBundle {
    var body: some Widget {
        PortfolioWidget()
        CoinPriceWidget()
    }
}
