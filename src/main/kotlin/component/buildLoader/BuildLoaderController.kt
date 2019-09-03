package component.buildLoader

import event.HideReportOverlay
import event.ReportLoaded
import event.ShowReportOverlay
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleMapProperty
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.transformation.FilteredList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import model.cucumber.Build
import model.property.ToggleProperty
import mu.KotlinLogging
import service.CucumberReportService
import tornadofx.Controller
import tornadofx.fail
import tornadofx.finally
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.mapProperty
import tornadofx.observableListOf
import tornadofx.observableMapOf
import tornadofx.property
import tornadofx.success
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
private const val SECOND_IN_MILLISECONDS = 1000
private const val MINUTE_IN_MILLISECONDS = 60 * SECOND_IN_MILLISECONDS
private const val HOUR_IN_MILLISECONDS = 60 * MINUTE_IN_MILLISECONDS
private const val DAY_IN_MILLISECONDS = 24 * HOUR_IN_MILLISECONDS

class BuildLoaderController: Controller() {

    private val logger = KotlinLogging.logger {}
    private val cucumberReportService: CucumberReportService by inject()

    private var selectedJob: String by property()
    val selectedJobProperty = getProperty(BuildLoaderController::selectedJob)

    private val jobList: ObservableList<String> by listProperty(observableListOf())
    val jobListProperty = SimpleListProperty(jobList)

    private var buildFilterValue: String by property()
    val buildFilterValueProperty = getProperty(BuildLoaderController::buildFilterValue)

    private var selectedBuild: Build by property<Build>()
    val selectedBuildProperty = getProperty(BuildLoaderController::selectedBuild)

    private val buildList: ObservableList<Build> by listProperty(observableListOf())
    private val filteredBuildList: FilteredList<Build> = FilteredList(buildList)
    val buildListProperty = SimpleListProperty(filteredBuildList)

    private val buildParameterMap: ObservableMap<String, String> by mapProperty(observableMapOf())
    val buildParameterMapProperty = SimpleMapProperty(buildParameterMap)

    val buildParameterResetProperty = ToggleProperty()

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

    fun onGetReportButtonClick(view: BuildLoaderView) = EventHandler<ActionEvent> {
        view.close()

        runAsync {
            fire(ShowReportOverlay())
            cucumberReportService.getReport(selectedBuild)
        } success {
            fire(ReportLoaded(it))
        } fail {
            logger.error(it) {}
        } finally {
            fire(HideReportOverlay())
        }
    }

    init {
        val jobs = cucumberReportService.getCucumberJobs()
        jobList.setAll(jobs.sorted())

        addSelectedBuildPropertyListener()
        addBuildFilterValuePropertyListener()
    }

    private fun addSelectedBuildPropertyListener() {
        selectedBuildProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                buildParameterMap.clear()
                buildParameterResetProperty.toggle()
                buildParameterMap["Revision"] = newValue.revision.toString()
                buildParameterMap["Finished"] = DATE_FORMAT.format(Date(newValue.timestamp))
                buildParameterMap["Duration"] = getDuration(newValue.duration)
                buildParameterMap.putAll(newValue.parameters)
            }
        }
    }

    private fun getDuration(duration: Long): String {
        var result = ""
        var remainder = duration

        if (remainder > DAY_IN_MILLISECONDS) {
            result = "${remainder / DAY_IN_MILLISECONDS} day(s)"
            remainder %= DAY_IN_MILLISECONDS
        }

        if (remainder > HOUR_IN_MILLISECONDS) {
            result = "$result ${remainder / HOUR_IN_MILLISECONDS} hour(s)"
            remainder %= HOUR_IN_MILLISECONDS
        }

        if (remainder > MINUTE_IN_MILLISECONDS) {
            result = "$result ${remainder / MINUTE_IN_MILLISECONDS} min(s)"
            remainder %= MINUTE_IN_MILLISECONDS
        }

        if (remainder > 0) {
            result = "$result ${remainder / SECOND_IN_MILLISECONDS} sec(s)"
        }

        return result.trim()
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