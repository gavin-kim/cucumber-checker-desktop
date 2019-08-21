package event

import model.Scenario
import model.Step
import tornadofx.EventBus
import tornadofx.FXEvent

class DisplayScenarioDetails(val scenario: Scenario) : FXEvent(EventBus.RunOn.ApplicationThread)