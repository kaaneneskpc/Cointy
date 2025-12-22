import SwiftUI
import BackgroundTasks

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase
    init() {
        registerBackgroundTasks()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .background {
                scheduleAppRefresh()
            }
        }
    }
    private func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: "com.kaaneneskpc.cointy.pricealert.refresh",
            using: nil
        ) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }
    }
    private func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: "com.kaaneneskpc.cointy.pricealert.refresh")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60)
        do {
            try BGTaskScheduler.shared.submit(request)
            print("Background task scheduled successfully")
        } catch {
            print("Could not schedule app refresh: \(error)")
        }
    }
    private func handleAppRefresh(task: BGAppRefreshTask) {
        scheduleAppRefresh()
        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }
        task.setTaskCompleted(success: true)
    }
}