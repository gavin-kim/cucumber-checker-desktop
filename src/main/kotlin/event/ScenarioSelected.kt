package event

import model.Scenario
import model.Step
import tornadofx.EventBus
import tornadofx.FXEvent

class ScenarioSelected(val scenario: Scenario, val backgroundSteps: List<Step>) : FXEvent(EventBus.RunOn.ApplicationThread)