package event

import component.scenarioTable.ScenarioTableFilter
import tornadofx.EventBus
import tornadofx.FXEvent

class DispatchScenarioTableFilter(val scenarioTableFilter: ScenarioTableFilter) : FXEvent(EventBus.RunOn.ApplicationThread)