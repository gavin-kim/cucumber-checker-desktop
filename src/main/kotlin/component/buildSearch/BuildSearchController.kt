package component.buildSearch

import event.DisplayReport
import event.HideReportOverlay
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
import tornadofx.*

class BuildSearchController: Controller() {

    private val logger = KotlinLogging.logger { }
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
            logger.info("Selected Job: $selectedJob, SelectedBuild: ${selectedBuild.id}")
            cucumberReportService.getReport(selectedJob, selectedBuild.id)
        } success {
            fire(DisplayReport(it))
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

        jobList.setAll(trunkJobs + maintenanceJobs)

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