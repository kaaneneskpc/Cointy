//
//  iosAppWidgetBundle.swift
//  iosAppWidget
//
//  Created by Kaan Enes KAPICI on 22.12.2025.
//

import WidgetKit
import SwiftUI

@main
struct iosAppWidgetBundle: WidgetBundle {
    var body: some Widget {
        PortfolioWidget()
        CoinPriceWidget()
    }
}
