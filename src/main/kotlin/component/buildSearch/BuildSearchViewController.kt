package component.buildSearch

import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import model.Build
import model.Job
import mu.KotlinLogging
import tornadofx.*

class BuildSearchViewController: Controller() {

    private val logger = KotlinLogging.logger {}

    private var selectedJob: Job by property<Job>()
    val selectedJobProperty = getProperty(BuildSearchViewController::selectedJob)

    private var buildSearchBar: String by property()
    val buildSearchBarProperty = getProperty(BuildSearchViewController::buildSearchBar).apply {
        addListener { _, oldValue, newValue ->
            filteredBuildList.setPredicate {
                if (newValue.isNullOrBlank() || oldValue == newValue) true
                else it.name.contains(newValue, true)
            }
        }
    }

    private var selectedBuild: Build by property<Build>()
    val selectedBuildProperty = getProperty(BuildSearchViewController::selectedBuild)

    private var buildList: ObservableList<Build> by listProperty(observableListOf())
    private val filteredBuildList: FilteredList<Build> = FilteredList(buildList)
    val filteredBuildListProperty = SimpleListProperty<Build>(filteredBuildList)

    fun updateBuilds(builds: Collection<Build>) {
        buildList.setAll(builds)
    }
}