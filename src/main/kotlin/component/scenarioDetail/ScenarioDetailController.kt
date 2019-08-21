package component.scenarioDetail

import event.ClearScenarioDetails
import event.DisplayScenarioDetails
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Scenario
import model.Step
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.property

class ScenarioDetailController : Controller() {

    private val scenarioGroupList: ObservableList<ScenarioDetailGroup> by listProperty(observableListOf())
    val scenarioGroupListProperty = SimpleListProperty(scenarioGroupList)

    private val beforeHookList: ObservableList<ScenarioDetail> by listProperty(observableListOf())
    val beforeHookListProperty = SimpleListProperty(beforeHookList)

    private val afterHookList: ObservableList<ScenarioDetail> by listProperty(observableListOf())
    val afterHookListProperty = SimpleListProperty(afterHookList)

    private val backgroundStepList: ObservableList<ScenarioDetail> by listProperty(observableListOf())
    val backgroundStepListProperty = SimpleListProperty(backgroundStepList)

    private val stepList: ObservableList<ScenarioDetail> by listProperty(observableListOf())
    val stepListProperty = SimpleListProperty(stepList)

    private var updated: Boolean by property(true)
    val updatedProperty = getProperty(ScenarioDetailController::updated)

    init {
        scenarioGroupList.setAll(
            ScenarioDetailGroup.BEFORE_HOOKS,
            ScenarioDetailGroup.BACKGROUND_STEPS,
            ScenarioDetailGroup.STEPS,
            ScenarioDetailGroup.AFTER_HOOKS
        )

        subscribe<DisplayScenarioDetails> {
            updateScenario(it.scenario)
        }

        subscribe<ClearScenarioDetails> {
            clearScenario()
        }
    }

    private fun updateScenario(scenario: Scenario) {

        val (beforeHooks, afterHooks) = scenario.hooks.partition { hook -> hook.keyword == Step.Keyword.BEFORE }

        beforeHookList.setAll(beforeHooks.map { hook -> buildScenarioDetailModel(hook) })
        afterHookList.setAll(afterHooks.map { hook -> buildScenarioDetailModel(hook) })
        backgroundStepList.setAll(scenario.backgroundSteps.map { step -> buildScenarioDetailModel(step) })
        stepList.setAll(scenario.steps.map { step -> buildScenarioDetailModel(step) })
        updated = !updated
    }

    private fun clearScenario() {
        beforeHookList.clear()
        afterHookList.clear()
        backgroundStepList.clear()
        stepList.clear()
        updated = !updated
    }

    private fun buildScenarioDetailModel(step: Step): ScenarioDetail {
        return ScenarioDetail(
            step.keyword.text,
            step.name,
            step.duration,
            step.result,
            step.messages,
            step.arguments
        )
    }
}