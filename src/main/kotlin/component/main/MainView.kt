package component.main

import mu.KotlinLogging
import tornadofx.*
import component.buildSearch.BuildSearchView
import component.report.ReportView

class MainView: View() {

    private val logger = KotlinLogging.logger {}

    private val buildSearchView: BuildSearchView by inject()
    private val reportView: ReportView by inject()

    override val root = borderpane {
        this.

        left {
            add(buildSearchView)
        }

        center {
            add(reportView)
        }
    }
}