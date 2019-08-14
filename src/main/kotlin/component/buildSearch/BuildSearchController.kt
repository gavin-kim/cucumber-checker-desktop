package component.buildSearch

import event.HideReportOverlay
import event.ReportLoaded
import event.ShowReportOverlay
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import model.Build
import model.View
import mu.KotlinLogging
import service.CucumberReportService
import tornadofx.Controller
import tornadofx.fail
import tornadofx.finally
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.property
import tornadofx.success

class BuildSearchController: Controller() {

    private val logger = KotlinLogging.logger {}
    private val cucumberReportService: CucumberReportService by inject()

    private var selectedJob: String by property()
    val selectedJobProperty = getProperty(BuildSearchController::selectedJob)

    private val jobList: ObservableList<String> by listProperty(observableListOf())
    val jobListProperty = SimpleListProperty(jobList)

    private var buildFilterValue: String by property()
    val buildFilterValueProperty = getProperty(BuildSearchController::buildFilterValue)

    private var selectedBuild: Build by property<Build>()
    val selectedBuildProperty = getProperty(BuildSearchController::selectedBuild)

    private val buildList: ObservableList<Build> by listProperty(observableListOf())
    private val filteredBuildList: FilteredList<Build> = FilteredList(buildList)
    val buildListProperty = SimpleListProperty(filteredBuildList)


    fun onJobListChange(comboBox: ComboBox<String>) = EventHandler<ActionEvent> {
        comboBox.isDisable = true

        runAsync {
            cucumberReportService.getBuilds(selectedJob)
        } success {
            val finishedBuilds = it.filter { build -> build.finished }
            buildList.setAll(finishedBuilds)
        } fail {
            buildList.setAll(emptyList())
            logger.error(it) {}
        } finally {
            comboBox.isDisable = false
        }
    }

    fun onGetReportButtonClick(button: Button) = EventHandler<ActionEvent> {
        button.isDisable = true

        runAsync {
            fire(ShowReportOverlay())
            cucumberReportService.getReport(selectedBuild)
        } success {
            fire(ReportLoaded(it))
        } fail {
            logger.error(it) {}
        } finally {
            fire(HideReportOverlay())
            button.isDisable = false
        }
    }

    init {
        val trunkJobs = cucumberReportService.getCucumberJobs(View.MANUAL_VALIDATION_ON_TRUNK)
        val maintenanceJobs = cucumberReportService.getCucumberJobs(View.MANUAL_VALIDATION_ON_MAINT)
        val cucumberUIJobs = cucumberReportService.getCucumberJobs(View.CUCUMBER_UI_AUTOMATION)

        jobList.setAll(trunkJobs + maintenanceJobs + cucumberUIJobs)
        jobList.sort()

        addBuildFilterValuePropertyListener()
    }

    private fun addBuildFilterValuePropertyListener() {
        buildFilterValueProperty.addListener { _, oldValue, newValue ->
            filteredBuildList.setPredicate {
                if (newValue.isNullOrBlank() || oldValue == newValue) true
                else it.text.contains(newValue, true)
            }
        }
    }
}