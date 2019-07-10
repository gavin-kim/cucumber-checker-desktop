package component.scenarioDetail

import event.ReportEvent
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Hook
import model.Scenario
import model.Step
import tornadofx.*

class ScenarioDetailController : Controller() {

    private lateinit var scenario: Scenario
    private lateinit var backgroundSteps: List<Step>

    private val scenarioDetailModel: ObservableList<ScenarioDetailModel> by listProperty(observableListOf())
    val scenarioDetailModelProperty = SimpleListProperty(scenarioDetailModel)

    init {
        subscribe<ReportEvent.ScenarioSelected> {
            scenarioDetailModel.setAll(ScenarioDetailModel(
                it.scenario.hooks.filter { hook -> hook.keyword == Hook.Keyword.BEFORE },
                it.backgroundSteps,
                it.scenario.steps,
                it.scenario.hooks.filter { hook -> hook.keyword == Hook.Keyword.AFTER }
            ))
        }
    }
}