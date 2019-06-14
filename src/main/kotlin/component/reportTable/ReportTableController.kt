package component.reportTable

import event.DispatchReportEvent
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.CucumberReport
import model.Feature

import tornadofx.*

class ReportTableController: Controller() {

    init {
        subscribe<DispatchReportEvent> {
            report = it.report
            failureList.setAll(it.report.failedFeatures)
        }
    }

    private lateinit var report: CucumberReport

    private var failureList: ObservableList<Feature> by listProperty(mutableListOf<Feature>().asObservable())
    val failureListProperty = SimpleListProperty(failureList)

    fun buildScreenShotLinks(files: List<String>): List<String> {
        return files.map { "${report.build.link}/cucumber-html-reports/$it" }
    }
}