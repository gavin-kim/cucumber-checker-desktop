package component.main

import mu.KotlinLogging
import tornadofx.*
import component.buildSearch.BuildSearchView
import component.report.ReportView
import component.scenarioTable.ScenarioTableView
import javafx.scene.layout.Priority

class MainView: View("MainView") {

    private val logger = KotlinLogging.logger {}

    override val root = hbox {

        add(BuildSearchView::class)
        add(ReportView::class)

        style {
            backgroundColor += c("#00FF00")
        }
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}