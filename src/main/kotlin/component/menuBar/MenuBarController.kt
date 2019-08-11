package component.menuBar

import event.DisplayReport
import event.ReportLoaded
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.Report
import service.PersistenceService
import tornadofx.Controller
import tornadofx.chooseDirectory

class MenuBarController : Controller() {

    private val persistenceService: PersistenceService by inject()

    private lateinit var loadedReport: Report

    init {
        subscribe<ReportLoaded> {
            loadedReport = it.report
            fire(DisplayReport(it.report))
        }
    }

    val onLoadReportButtonClicked = EventHandler<MouseEvent> {
        val directory = chooseDirectory("Load Report")

        if (directory != null) {
            loadedReport = persistenceService.load(directory)
            fire(DisplayReport(loadedReport))
        }
    }

    val onSaveReportButtonClicked = EventHandler<MouseEvent> {
        val directory = chooseDirectory("Save Report")

        if (directory != null) {
            persistenceService.save(loadedReport, directory)
        }
    }
}