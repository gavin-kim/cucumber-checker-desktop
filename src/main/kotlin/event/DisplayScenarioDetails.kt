package event

import model.cucumber.Scenario
import tornadofx.EventBus
import tornadofx.FXEvent

class DisplayScenarioDetails(
    val scenario: Scenario,
    val featureTags: List<String>,
    val scenarioTags: List<String>,
    val errorMessages: List<String>
) : FXEvent(EventBus.RunOn.ApplicationThread)