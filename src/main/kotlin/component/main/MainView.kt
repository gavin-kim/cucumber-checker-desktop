package component.main

import component.buildSearch.BuildSearchView
import component.menuBar.MenuBarView
import component.report.ReportView
import component.statusBar.StatusBarView
import mu.KotlinLogging
import tornadofx.View
import tornadofx.borderpane
import tornadofx.fitToParentSize

class MainView : View("MainView") {

    private val logger = KotlinLogging.logger {}
    private val viewTitle = app.config.string("main.title", "Cucumber Checker")

    override val root = borderpane {
        title = viewTitle

        top(MenuBarView::class)
        left(BuildSearchView::class)
        center(ReportView::class)
        bottom(StatusBarView::class)
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}