package component.menuBar

import component.scenarioTable.ScenarioTableFilter
import event.DispatchScenarioTableFilter
import event.DisplayReport
import event.ReportLoaded
import javafx.event.ActionEvent
import javafx.event.EventHandler
import model.cucumber.Report
import service.PersistenceService
import tornadofx.Controller
import tornadofx.chooseDirectory
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property

class MenuBarController : Controller() {

    private val persistenceService: PersistenceService by inject()

    private lateinit var loadedReport: Report

    private var showUnstable: Boolean by property(false)
    val showUnstableProperty = getProperty(MenuBarController::showUnstable)

    init {
        subscribe<ReportLoaded> {
            loadedReport = it.report
            fire(DisplayReport(it.report))
            fire(DispatchScenarioTableFilter(ScenarioTableFilter(showUnstable)))
        }

        showUnstableProperty.onChange { fire(DispatchScenarioTableFilter(ScenarioTableFilter(showUnstable))) }
    }

    val onLoadReportButtonClicked = EventHandler<ActionEvent> {
        val directory = chooseDirectory("Load Report")

        if (directory != null) {
            loadedReport = persistenceService.load(directory)
            fire(DisplayReport(loadedReport))
        }
    }

    val onSaveReportButtonClicked = EventHandler<ActionEvent> {
        val directory = chooseDirectory("Save Report")

        if (directory != null) {
            persistenceService.save(loadedReport, directory)
        }
    }

    val onCompareButtonClicked = EventHandler<ActionEvent> {

    }
}