package controller

import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Build
import model.Feature
import model.Job
import service.CucumberReportService
import tornadofx.*

class ReportViewController: Controller() {

    private val cucumberReportService by lazy { CucumberReportService() }

    private var failureList: ObservableList<Feature> by listProperty(mutableListOf<Feature>().asObservable())
    fun failureListProperty() = SimpleListProperty(failureList)

    fun loadReport(job: Job, build: Build) {
        val report = cucumberReportService.getReport(job, build)
        failureList.setAll(report.failedFeatures)
    }
}