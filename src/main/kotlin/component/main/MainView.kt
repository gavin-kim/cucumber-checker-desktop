package component.main

import component.menuBar.MenuBarView
import component.report.ReportView
import component.statusBar.StatusBarView
import javafx.scene.image.Image
import mu.KotlinLogging
import tornadofx.View
import tornadofx.addStageIcon
import tornadofx.borderpane
import tornadofx.fitToParentSize

class MainView : View("MainView") {

    private val logger = KotlinLogging.logger {}
    private val mainIcon = app.config.string("main.icon")
    private val mainTitle = app.config.string("main.title", "Cucumber Checker")

    override val root = borderpane {
        addStageIcon(Image(mainIcon))

        title = mainTitle

        top(MenuBarView::class)
        center(ReportView::class)
        bottom(StatusBarView::class)
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}