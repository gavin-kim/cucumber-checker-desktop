package component.scenarioTable

import event.ClearScenarioDetails
import event.DispatchScenarioTableFilter
import event.DisplayReport
import event.DisplayScenarioDetails
import event.HideReportOverlay
import fragment.ScreenShotFragment
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.TableView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Modality
import javafx.stage.StageStyle
import model.cucumber.Report
import model.cucumber.Scenario
import model.cucumber.Step
import tornadofx.Controller
import tornadofx.SortedFilteredList
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.property
import tornadofx.selectedValue

private val STEP_PARAMETER_REGEX = "[\"][^\"]*[\"]".toRegex()
private const val STEP_PARAMETER = "\"?\""

class ScenarioTableController: Controller() {

    private lateinit var report: Report

    private var selectedScenarioTableRow: ScenarioTableRow by property()
    val selectedReportRowProperty = getProperty(ScenarioTableController::selectedScenarioTableRow)

    private val scenarioTableRowList: ObservableList<ScenarioTableRow> by listProperty(observableListOf())
    val filteredScenarioTableRowList: SortedFilteredList<ScenarioTableRow> = SortedFilteredList(scenarioTableRowList)

    val onKeyPressed = EventHandler<KeyEvent> {
        when {
            it.isControlDown && it.code === KeyCode.C -> copySelectionToClipboard(it.source)
        }
    }

    private fun copySelectionToClipboard(source: Any) {
        if (source is TableView<*>) {
            val clipboardContent = ClipboardContent()

            clipboardContent.putString(source.selectedValue.toString())
            Clipboard.getSystemClipboard().setContent(clipboardContent)
        }
    }

    fun onScreenShotLinkClick(link: String) = EventHandler<ActionEvent> {
        find<ScreenShotFragment>(
            ScreenShotFragment::reportType to report.type,
            ScreenShotFragment::link to link
        ).openModal(StageStyle.UNDECORATED, Modality.NONE, resizable = true)
    }

    init {
        subscribe<DisplayReport> {
            report = it.report

            val scenarioTableRows = buildScenarioTableRows(it.report)
            scenarioTableRowList.setAll(scenarioTableRows)

            fire(ClearScenarioDetails())
            fire(HideReportOverlay())
        }

        subscribe<DispatchScenarioTableFilter> {
            updateFilteredScenarioTableRowList(it.scenarioTableFilter)
        }

        addSelectedFeaturePropertyListener()
    }

    private fun buildScenarioTableRows(report: Report): List<ScenarioTableRow> {
        return report.failedFeatures.flatMap { feature ->

            feature.failedScenarios.map { scenario ->

                val firstFailedStep = getFirstFailedStep(scenario)

                ScenarioTableRow(
                    scenario = scenario,
                    featureName = feature.name,
                    featureTags = feature.tags,
                    scenarioName = scenario.name,
                    scenarioTags = scenario.tags,
                    screenShotLinks = scenario.screenShotFiles.map { "${report.path}/$it" },
                    failedStep = removeStepParameters(firstFailedStep.name),
                    messages = firstFailedStep.messages,
                    unstable = scenario.unstable
                )
            }
        }
    }

    private fun getFirstFailedStep(scenario: Scenario): Step {

        val (failedBeforeHooks, failedAfterHooks) = scenario.hooks
            .filter { it.canCauseFailure }
            .partition { it.keyword == Step.Keyword.BEFORE }

        val failedBackgroundSteps = scenario.backgroundSteps
            .filter { it.canCauseFailure }

        val failedSteps = scenario.steps
            .filter { it.canCauseFailure }

        return when {
            failedBeforeHooks.isNotEmpty() -> failedBeforeHooks.first()
            failedBackgroundSteps.isNotEmpty() -> failedBackgroundSteps.first()
            failedSteps.isNotEmpty() -> failedSteps.first()
            failedAfterHooks.isNotEmpty() -> failedAfterHooks.first()
            else -> throw IllegalStateException("Cannot find failed step")
        }
    }

    private fun removeStepParameters(stepName: String): String {
        return stepName.replace(STEP_PARAMETER_REGEX, STEP_PARAMETER)
    }

    private fun addSelectedFeaturePropertyListener() {
        selectedReportRowProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                fire(DisplayScenarioDetails(
                    newValue.scenario,
                    newValue.featureTags,
                    newValue.scenarioTags,
                    newValue.messages
                ))
            }
        }
    }

    private fun updateFilteredScenarioTableRowList(scenarioTableFilter: ScenarioTableFilter) {
        filteredScenarioTableRowList.predicate = {
            if (scenarioTableFilter.showUnstableTests) true
            else it.unstable.not()
        }
    }
}