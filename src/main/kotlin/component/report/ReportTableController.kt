package component.report

import event.DispatchReportEvent
import fragment.ScreenShotFragment
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import model.Report
import model.Result

import tornadofx.*

class ReportTableController: Controller() {

    private lateinit var reportViewModel: ReportViewModel

    private val failureList: ObservableList<FeatureViewModel> by listProperty(observableListOf())
    val failureListProperty = SimpleListProperty(failureList)

    fun onScreenShotLinkClick(link: String) = EventHandler<ActionEvent> {
        find<ScreenShotFragment>(ScreenShotFragment::link to link)
            .openModal(StageStyle.UNDECORATED, Modality.NONE, resizable = true)
    }

    private fun convertReportToViewModel(report: Report): ReportViewModel {
        val featureViewModels = report.failedFeatures.flatMap { feature ->

            val failedBackgroundStep = feature.backgroundSteps.find { it.result == Result.FAILED }

            feature.failedScenarios.map { scenario ->

                val failedStep = failedBackgroundStep ?: scenario.steps.find { it.result == Result.FAILED }
                val failedHooks = scenario.hooks.filter { it.result == Result.FAILED }

                FeatureViewModel(
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

        return ReportViewModel(report.jobName, report.buildId, report.buildUrl, featureViewModels)
    }

    init {
        subscribe<DispatchReportEvent> {
            reportViewModel = convertReportToViewModel(it.report)
            failureList.setAll(reportViewModel.featureViewModels)
        }
    }
}

data class ReportViewModel(
    val jobName: String,
    val buildId: Int,
    val buildUrl: String,
    val featureViewModels: List<FeatureViewModel>
)

data class FeatureViewModel(
    val featureName: String,
    val featureTags: String,
    val scenarioName: String,
    val scenarioTags: String,
    val screenShotLinks: List<String>,
    val failedSpot: String,
    val failedStep: String,
    val failedHooks: String
)