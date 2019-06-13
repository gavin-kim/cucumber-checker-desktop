package component.main

import mu.KotlinLogging
import tornadofx.*
import component.buildSearch.BuildSearchView
import component.reportTable.ReportTableView

class MainView: View() {

    private val logger = KotlinLogging.logger {}

    private val buildSearchView: BuildSearchView by inject()
    private val reportView: ReportTableView by inject()

    override val root = borderpane {

        left {
            add(buildSearchView)
        }

        center {
            add(reportView)
        }
    }
}