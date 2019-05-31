package view.buildSearchView

import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Build
import model.Job
import mu.KotlinLogging
import tornadofx.*

class BuildSearchViewModel: Controller() {

    private val logger = KotlinLogging.logger {}

    private var selectedJob: Job by property<Job>()
    fun selectedJobProperty() = getProperty(BuildSearchViewModel::selectedJob)

    private var selectedBuild: Build by property<Build>()
    fun selectedBuildProperty() = getProperty(BuildSearchViewModel::selectedBuild)

    private var buildList: ObservableList<Build> by listProperty(mutableListOf<Build>().asObservable())
    fun buildListProperty() = SimpleListProperty<Build>(buildList)

    fun updateBuilds(builds: Collection<Build>) {
        buildList.setAll(builds)
    }
}