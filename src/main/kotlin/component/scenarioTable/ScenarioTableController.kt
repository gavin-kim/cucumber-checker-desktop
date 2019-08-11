package component.scenarioTable

import component.reportFilter.ReportFilterData
import event.DispatchReportFilterData
import event.DisplayReport
import event.DisplayScenarioDetail
import event.HideReportOverlay
import fragment.ScreenShotFragment
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.TableView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Modality
import javafx.stage.StageStyle
import model.Feature
import model.Report
import model.Scenario
import model.Step
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.property
import tornadofx.selectedValue
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.associateBy
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.toMap
import kotlin.collections.toSet

class ScenarioTableController: Controller() {

    private lateinit var report: Report
    private lateinit var featureMap: Map<String, Feature>
    private lateinit var scenarioMap: Map<Pair<String/*Feature Name*/, String/*Scenario Name*/>, Scenario>

    private var selectedScenarioTableRow: ScenarioTableRow by property()
    val selectedReportRowProperty = getProperty(ScenarioTableController::selectedScenarioTableRow)

    private val scenarioTableRowList: ObservableList<ScenarioTableRow> by listProperty(observableListOf())
    private val filteredScenarioTableRowList: FilteredList<ScenarioTableRow> = FilteredList(scenarioTableRowList)
    val scenarioRowTableRowListProperty = SimpleListProperty(filteredScenarioTableRowList)

    private val columnVisiblePropertyMap: MutableMap<ScenarioTableColumn, BooleanProperty> = mutableMapOf()

    val onKeyPressed = EventHandler<KeyEvent> {
        when {
            it.isControlDown && it.code === KeyCode.C -> copySelectionToClipboard(it.source as TableView<ScenarioTableRow>)
        }
    }

    private fun copySelectionToClipboard(tableView: TableView<ScenarioTableRow>) {
        val clipboardContent = ClipboardContent()

        clipboardContent.putString(tableView.selectedValue.toString())
        Clipboard.getSystemClipboard().setContent(clipboardContent)
    }

    fun onScreenShotLinkClick(link: String) = EventHandler<ActionEvent> {
        find<ScreenShotFragment>(
            ScreenShotFragment::reportType to report.type,
            ScreenShotFragment::link to link
        ).openModal(StageStyle.UNDECORATED, Modality.NONE, resizable = true)
    }

    fun setColumnVisibleProperty(column: ScenarioTableColumn, visibleProperty: BooleanProperty) {
        columnVisiblePropertyMap[column] = visibleProperty;
    }

    init {
        subscribe<DisplayReport> {
            report = it.report
            featureMap = getFeatureMap(it.report)
            scenarioMap = getScenarioMap(it.report)

            val scenarioTableRows = buildScenarioTableRows(it.report)
            scenarioTableRowList.setAll(scenarioTableRows)

            fire(HideReportOverlay())
        }

        subscribe<DispatchReportFilterData> {
            updateFilteredScenarioTableRowList(it.reportFilterData)
            updateColumnVisibleProperties(it.reportFilterData.displayColumns)
        }

        addSelectedFeaturePropertyListener()
    }

    private fun buildScenarioTableRows(report: Report): List<ScenarioTableRow> {
        return report.failedFeatures.flatMap { feature ->

            val failedBackgroundSteps = feature.backgroundSteps.filter { it.result == Step.Result.FAILED }

            feature.failedScenarios.map { scenario ->

                val failedSteps = scenario.steps.filter { it.result == Step.Result.FAILED }
                val failedHooks = scenario.hooks.filter { it.result == Step.Result.FAILED }

                ScenarioTableRow(
                    featureName = feature.name,
                    featureTags = feature.tags.joinToString(),
                    scenarioName = scenario.name,
                    scenarioTags = scenario.tags.joinToString(),
                    screenShotLinks = scenario.screenShotFiles.map { "${report.path}/$it" },
                    failedSpot = when {
                        failedBackgroundSteps.isNotEmpty() -> "Background Step"
                        failedSteps.isNotEmpty() -> "Step"
                        failedHooks.isNotEmpty() -> "Hook"
                        else -> "Unknown"
                    },
                    failedSteps = (failedBackgroundSteps + failedSteps + failedHooks).joinToString { "${it.keyword.text} ${it.name}" },
                    unstable = scenario.unstable
                )
            }
        }
    }

    private fun addSelectedFeaturePropertyListener() {
        selectedReportRowProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                val feature = checkNotNull(featureMap[newValue.featureName])
                val scenario = checkNotNull(scenarioMap[newValue.featureName to newValue.scenarioName])

                fire(DisplayScenarioDetail(scenario, feature.backgroundSteps))
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

    private fun updateFilteredScenarioTableRowList(reportFilterData: ReportFilterData) {
        filteredScenarioTableRowList.setPredicate {
            if (reportFilterData.showUnstableTests) true
            else it.unstable.not()
        }
    }

    private fun updateColumnVisibleProperties(displayColumns: Collection<ScenarioTableColumn>) {
        val displayColumnSet = displayColumns.toSet()

        columnVisiblePropertyMap.forEach { (column, visibilityProperty) ->
            visibilityProperty.set(displayColumnSet.contains(column))
        }
    }
}