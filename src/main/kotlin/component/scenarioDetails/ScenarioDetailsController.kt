package component.scenarioDetails

import event.ClearScenarioDetails
import event.DisplayScenarioDetails
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.cucumber.Step
import model.property.ToggleProperty
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.property


class ScenarioDetailsController : Controller() {

    private val scenarioGroupList: ObservableList<ScenarioDetailsGroup> by listProperty(observableListOf())
    val scenarioGroupListProperty = SimpleListProperty(scenarioGroupList)

    private val beforeHookList: ObservableList<ScenarioDetails> by listProperty(observableListOf())
    val beforeHookListProperty = SimpleListProperty(beforeHookList)

    private val afterHookList: ObservableList<ScenarioDetails> by listProperty(observableListOf())
    val afterHookListProperty = SimpleListProperty(afterHookList)

    private val backgroundStepList: ObservableList<ScenarioDetails> by listProperty(observableListOf())
    val backgroundStepListProperty = SimpleListProperty(backgroundStepList)

    private val stepList: ObservableList<ScenarioDetails> by listProperty(observableListOf())
    val stepListProperty = SimpleListProperty(stepList)

    private var featureTags: String by property()
    val featureTagsProperty = getProperty(ScenarioDetailsController::featureTags)

    private var scenarioTags: String by property()
    val scenarioTagsProperty = getProperty(ScenarioDetailsController::scenarioTags)

    private var errorMessage: String by property()
    val errorMessageProperty = getProperty(ScenarioDetailsController::errorMessage)

    val modelUpdatedProperty = ToggleProperty()

    init {
        scenarioGroupList.setAll(
            ScenarioDetailsGroup.BEFORE_HOOKS,
            ScenarioDetailsGroup.BACKGROUND_STEPS,
            ScenarioDetailsGroup.STEPS,
            ScenarioDetailsGroup.AFTER_HOOKS
        )

        subscribe<DisplayScenarioDetails> {
            updateModel(it)
        }

        subscribe<ClearScenarioDetails> {
            clearModel()
        }
    }

    private fun updateModel(model: DisplayScenarioDetails) {
        val scenario = model.scenario
        val (beforeHooks, afterHooks) = scenario.hooks.partition { hook -> hook.keyword == Step.Keyword.BEFORE }

        beforeHookList.setAll(beforeHooks.map { hook -> buildScenarioDetailModel(hook) })
        afterHookList.setAll(afterHooks.map { hook -> buildScenarioDetailModel(hook) })
        backgroundStepList.setAll(scenario.backgroundSteps.map { step -> buildScenarioDetailModel(step) })
        stepList.setAll(scenario.steps.map { step -> buildScenarioDetailModel(step) })

        featureTags = model.featureTags.joinToString()
        scenarioTags = model.scenarioTags.joinToString()
        errorMessage = model.errorMessages.joinToString("\n")

        modelUpdatedProperty.toggle()
    }

    private fun clearModel() {
        beforeHookList.clear()
        afterHookList.clear()
        backgroundStepList.clear()
        stepList.clear()

        featureTags = ""
        scenarioTags = ""
        errorMessage = ""

        modelUpdatedProperty.toggle()
    }

    private fun buildScenarioDetailModel(step: Step): ScenarioDetails {
        return ScenarioDetails(
            step.keyword.text,
            step.name,
            step.duration,
            step.result,
            step.messages,
            step.arguments
        )
    }
}