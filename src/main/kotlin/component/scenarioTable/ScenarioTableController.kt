package component.scenarioTable

import event.BuildSearchEvent
import event.ReportEvent
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

    private var displayOverlay: Boolean by property()
    val displayOverlayProperty = getProperty(ScenarioTableController::displayOverlay)

    private var selectedScenarioTableRowModel: ScenarioTableRowModel by property()
    val selectedReportRowProperty = getProperty(ScenarioTableController::selectedScenarioTableRowModel)

    private val scenarioTableRowModelList: ObservableList<ScenarioTableRowModel> by listProperty(observableListOf())
    val reportRowModelListProperty = SimpleListProperty(scenarioTableRowModelList)

    fun onScreenShotLinkClick(link: String) = EventHandler<ActionEvent> {
        find<ScreenShotFragment>(ScreenShotFragment::link to link)
            .openModal(StageStyle.UNDECORATED, Modality.NONE, resizable = true)
    }

    private fun buildFeatureViewModels(report: Report): List<ScenarioTableRowModel> {
        return report.failedFeatures.flatMap { feature ->

            val failedBackgroundStep = feature.backgroundSteps.find { it.result == Result.FAILED }

            feature.failedScenarios.map { scenario ->

                val failedStep = failedBackgroundStep ?: scenario.steps.find { it.result == Result.FAILED }
                val failedHooks = scenario.hooks.filter { it.result == Result.FAILED }

                ScenarioTableRowModel(
                    featureName = feature.name,
                    featureTags = feature.tags.joinToString(),
                    scenarioName = scenario.name,
                    scenarioTags = scenario.tags.joinToString(),
                    screenShotLinks = scenario.screenShotFiles.map { "${report.buildUrl}/cucumber-html-reports/$it" },
                    failedSpot = if (failedStep != null) "Step" else "Outside",
                    failedStep = failedStep?.name ?: "",
                    failedHooks = failedHooks.joinToString { "${it.keyword} ${it.name}" }
                )
            }
        }
    }

    init {
        subscribe<BuildSearchEvent.ReportButtonClick> {
            displayOverlay = true
        }

        subscribe<BuildSearchEvent.ReportLoaded> {
            displayOverlay = false

            report = it.report
            featureMap = getFeatureMap(it.report)
            scenarioMap = getScenarioMap(it.report)

            val featureViewModels = buildFeatureViewModels(it.report)
            scenarioTableRowModelList.setAll(featureViewModels)
        }

        addSelectedFeaturePropertyListener()
    }

    private fun addSelectedFeaturePropertyListener() {
        selectedReportRowProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                val feature = checkNotNull(featureMap[newValue.featureName])
                val scenario = checkNotNull(scenarioMap[newValue.featureName to newValue.scenarioName])

                fire(ReportEvent.ScenarioSelected(scenario, feature.backgroundSteps))
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