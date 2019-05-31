package view

import view.reportView.ReportViewModel
import mu.KotlinLogging
import tornadofx.*
import view.buildSearchView.BuildSearchView
import view.reportView.ReportView

class MainView: View() {

    private val logger = KotlinLogging.logger {}

    private val buildSearchView: BuildSearchView by inject()
    private val reportView: ReportView by inject()

    override val root = borderpane {

        left {
            add(buildSearchView)
        }

        center {
            add(reportView)
        }
    }
}