package controller

import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Build
import model.CucumberReport
import model.Job
import mu.KotlinLogging
import service.CucumberReportService
import tornadofx.*

class SearchViewController: Controller() {

    private val logger = KotlinLogging.logger {}

    private val cucumberReportService by lazy { CucumberReportService() }

    private var selectedJob: Job by property<Job>()
    fun selectedJobProperty() = getProperty(SearchViewController::selectedJob)

    private var selectedBuild: Build by property<Build>()
    fun selectedBuildProperty() = getProperty(SearchViewController::selectedBuild)

    private var buildList: ObservableList<Build> by listProperty(mutableListOf<Build>().asObservable())
    fun buildListProperty() = SimpleListProperty<Build>(buildList)

    fun getCucumberReport(): CucumberReport {
        return cucumberReportService.getReport(selectedJob, selectedBuild)
    }

    fun updateBuilds() {
        val builds = cucumberReportService.getBuilds(selectedJob)
        buildList.setAll(builds)
    }

    fun getBuilds(): List<Build> {
        return cucumberReportService.getBuilds(selectedJob)
    }
}