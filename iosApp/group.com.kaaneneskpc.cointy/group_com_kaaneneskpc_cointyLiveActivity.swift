//
//  group_com_kaaneneskpc_cointyLiveActivity.swift
//  group.com.kaaneneskpc.cointy
//
//  Created by Kaan Enes KAPICI on 22.12.2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct group_com_kaaneneskpc_cointyAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var emoji: String
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct group_com_kaaneneskpc_cointyLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: group_com_kaaneneskpc_cointyAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Hello \(context.state.emoji)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.emoji)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.emoji)")
            } minimal: {
                Text(context.state.emoji)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension group_com_kaaneneskpc_cointyAttributes {
    fileprivate static var preview: group_com_kaaneneskpc_cointyAttributes {
        group_com_kaaneneskpc_cointyAttributes(name: "World")
    }
}

extension group_com_kaaneneskpc_cointyAttributes.ContentState {
    fileprivate static var smiley: group_com_kaaneneskpc_cointyAttributes.ContentState {
        group_com_kaaneneskpc_cointyAttributes.ContentState(emoji: "😀")
     }
     
     fileprivate static var starEyes: group_com_kaaneneskpc_cointyAttributes.ContentState {
         group_com_kaaneneskpc_cointyAttributes.ContentState(emoji: "🤩")
     }
}

#Preview("Notification", as: .content, using: group_com_kaaneneskpc_cointyAttributes.preview) {
   group_com_kaaneneskpc_cointyLiveActivity()
} contentStates: {
    group_com_kaaneneskpc_cointyAttributes.ContentState.smiley
    group_com_kaaneneskpc_cointyAttributes.ContentState.starEyes
}
