package component.main

import component.buildSearch.BuildSearchView
import component.menu.MenuView
import component.report.ReportView
import mu.KotlinLogging
import tornadofx.View
import tornadofx.fitToParentSize
import tornadofx.hbox
import tornadofx.vbox

class MainView: View("MainView") {

    private val logger = KotlinLogging.logger {}
    private val viewTitle = app.config.string("main.title", "Cucumber Checker")

    override val root = vbox(2) {
        title = viewTitle

        add(MenuView::class)

        hbox(2) {
            add(BuildSearchView::class)
            add(ReportView::class)

            fitToParentSize()
        }
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}