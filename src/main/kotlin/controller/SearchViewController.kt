package controller

import model.CucumberReport
import model.Job
import mu.KotlinLogging
import service.CucumberReportService
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property

class SearchViewController: Controller() {

    private val logger = KotlinLogging.logger {}

    private val cucumberReportService by lazy { CucumberReportService() }

    private var selectedJob: Job by property(Job.MANUAL_ORACLE_JOB)
    fun selectedJobProperty() = getProperty(SearchViewController::selectedJob)

    private var buildId: Int by property<Int>()
    fun buildIdProperty() = getProperty(SearchViewController::buildId)

    fun getCucumberReport(): CucumberReport {
        val report = cucumberReportService.getReport(selectedJob, buildId)
        return report
    }
}