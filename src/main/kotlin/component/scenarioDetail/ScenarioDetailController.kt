package component.scenarioDetail

import event.ScenarioSelected
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Scenario
import model.Step
import tornadofx.*

class ScenarioDetailController : Controller() {

    private lateinit var scenario: Scenario

    private val stepGroups: ObservableList<StepDetailGroup> by listProperty(observableListOf())
    val stepGroupsProperty = SimpleListProperty(stepGroups)

    private val beforeHooks: ObservableList<StepDetail> by listProperty(observableListOf())
    val beforeHooksProperty = SimpleListProperty(beforeHooks)

    private val afterHooks: ObservableList<StepDetail> by listProperty(observableListOf())
    val afterHooksProperty = SimpleListProperty(afterHooks)

    private val backgroundSteps: ObservableList<StepDetail> by listProperty(observableListOf())
    val backgroundStepsProperty = SimpleListProperty(backgroundSteps)

    private val steps: ObservableList<StepDetail> by listProperty(observableListOf())
    val stepsProperty = SimpleListProperty(steps)

    private var updated: Boolean by property(true)
    val updatedProperty = getProperty(ScenarioDetailController::updated)

    init {
        subscribe<ScenarioSelected> {
            stepGroups.setAll(
                StepDetailGroup.BEFORE_HOOKS,
                StepDetailGroup.BACKGROUND_STEPS,
                StepDetailGroup.STEPS,
                StepDetailGroup.AFTER_HOOKS
            )

            beforeHooks.setAll(
                it.scenario.hooks
                    .filter { hook -> hook.keyword == Step.Keyword.BEFORE }
                    .map { hook -> buildScenarioDetailModel(hook) }
            )

            afterHooks.setAll(
                it.scenario.hooks
                    .filter { hook -> hook.keyword == Step.Keyword.AFTER }
                    .map { hook -> buildScenarioDetailModel(hook) }
            )

            backgroundSteps.setAll(it.backgroundSteps.map { step -> buildScenarioDetailModel(step) })
            steps.setAll(it.scenario.steps.map { step -> buildScenarioDetailModel(step) })
            updated = !updated
        }
    }

    private fun buildScenarioDetailModel(step: Step): StepDetail {
        return StepDetail(
            step.keyword.text,
            step.name,
            step.duration,
            step.result,
            step.messages,
            step.arguments
        )
    }
}