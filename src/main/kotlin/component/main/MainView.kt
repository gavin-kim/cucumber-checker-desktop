package component.main

import mu.KotlinLogging
import tornadofx.*
import component.buildSearch.BuildSearchView
import component.report.ReportTableView

class MainView: View("MainView") {

    private val logger = KotlinLogging.logger {}

    private val buildSearchView: BuildSearchView by inject()
    private val reportTableView: ReportTableView by inject()

    override val root = borderpane {
        left {
            add(buildSearchView)
        }

        center {
            add(reportTableView)
        }
    }
}