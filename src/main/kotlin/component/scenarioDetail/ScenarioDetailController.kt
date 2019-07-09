package component.scenarioDetail

import event.ReportEvent
import model.Scenario
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property

class ScenarioDetailController : Controller() {

    private var scenario: Scenario by property()
    val scenarioProperty = getProperty(ScenarioDetailController::scenario)

    init {
        subscribe<ReportEvent.ScenarioSelected> {
            scenario = it.scenario
        }
    }
}