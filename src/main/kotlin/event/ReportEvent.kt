package event

import model.Scenario
import tornadofx.EventBus
import tornadofx.FXEvent

sealed class ReportEvent {
    class ScenarioSelected(val scenario: Scenario) : FXEvent(EventBus.RunOn.ApplicationThread)
}