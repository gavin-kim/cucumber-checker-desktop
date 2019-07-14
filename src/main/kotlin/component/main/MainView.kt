package component.main

import component.buildSearch.BuildSearchView
import component.report.ReportView
import mu.KotlinLogging
import tornadofx.View
import tornadofx.fitToParentSize
import tornadofx.hbox

class MainView: View("MainView") {

    private val logger = KotlinLogging.logger {}

    override val root = hbox(2) {
        add(BuildSearchView::class)
        add(ReportView::class)
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}