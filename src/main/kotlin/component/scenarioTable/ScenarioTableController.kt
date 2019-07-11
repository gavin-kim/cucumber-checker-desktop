package component.scenarioTable

import event.ReportDisplayed
import event.ReportLoaded
import event.ScenarioSelected
import fragment.ScreenShotFragment
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import model.Feature
import model.Report
import model.Result
import model.Scenario

import tornadofx.*

class ScenarioTableController: Controller() {

    private lateinit var report: Report
    private lateinit var featureMap: Map<String, Feature>
    private lateinit var scenarioMap: Map<Pair<String/*Feature Name*/, String/*Scenario Name*/>, Scenario>

    private var selectedScenarioTableRow: ScenarioTableRow by property()
    val selectedReportRowProperty = getProperty(ScenarioTableController::selectedScenarioTableRow)

    private val scenarioTableRowList: ObservableList<ScenarioTableRow> by listProperty(observableListOf())
    val reportRowModelListProperty = SimpleListProperty(scenarioTableRowList)

    fun onScreenShotLinkClick(link: String) = EventHandler<ActionEvent> {
        find<ScreenShotFragment>(ScreenShotFragment::link to link)
            .openModal(StageStyle.UNDECORATED, Modality.NONE, resizable = true)
    }

    private fun buildFeatureViewModels(report: Report): List<ScenarioTableRow> {
        return report.failedFeatures.flatMap { feature ->

            val failedBackgroundSteps = feature.backgroundSteps.filter { it.result == Result.FAILED }

            feature.failedScenarios.map { scenario ->

                val failedSteps = scenario.steps.filter { it.result == Result.FAILED }
                val failedHooks = scenario.hooks.filter { it.result == Result.FAILED }

                ScenarioTableRow(
                    featureName = feature.name,
                    featureTags = feature.tags.joinToString(),
                    scenarioName = scenario.name,
                    scenarioTags = scenario.tags.joinToString(),
                    screenShotLinks = scenario.screenShotFiles.map { "${report.buildUrl}/cucumber-html-reports/$it" },
                    failedSpot = when {
                        failedBackgroundSteps.isNotEmpty() -> "Background Step"
                        failedSteps.isNotEmpty() -> "Step"
                        failedHooks.isNotEmpty() -> "Hook"
                        else -> "Unknown"
                    },
                    failedSteps = (failedBackgroundSteps + failedSteps + failedHooks).joinToString { "${it.keyword.text} ${it.name}" }
                )
            }
        }
    }

    init {
        subscribe<ReportLoaded> {
            report = it.report
            featureMap = getFeatureMap(it.report)
            scenarioMap = getScenarioMap(it.report)

            val featureViewModels = buildFeatureViewModels(it.report)
            scenarioTableRowList.setAll(featureViewModels)

            fire(ReportDisplayed())
        }

        addSelectedFeaturePropertyListener()
    }

    private fun addSelectedFeaturePropertyListener() {
        selectedReportRowProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                val feature = checkNotNull(featureMap[newValue.featureName])
                val scenario = checkNotNull(scenarioMap[newValue.featureName to newValue.scenarioName])

                fire(ScenarioSelected(scenario, feature.backgroundSteps))
            }
        }
    }

    private fun getFeatureMap(report: Report): Map<String, Feature> {
        return report.failedFeatures.associateBy { it.name }
    }

    private fun getScenarioMap(report: Report): Map<Pair<String, String>, Scenario> {
        return report.failedFeatures.flatMap { feature ->
            feature.failedScenarios.map { scenario ->  (feature.name to scenario.name) to scenario }
        }.toMap()
    }
}