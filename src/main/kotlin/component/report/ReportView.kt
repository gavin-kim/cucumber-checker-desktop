package component.report

import component.scenarioDetail.ScenarioDetailView
import component.scenarioTable.ScenarioTableView
import javafx.geometry.Pos
import tornadofx.*

class ReportView : View("ReportView") {

    private val controller: ReportController by inject()

    private val scenarioTableView: ScenarioTableView by inject()
    private val scenarioDetailView: ScenarioDetailView by inject()

    private val overlayContainer = stackpane { add(MaskPane()) }

    override val root = vbox {
        fitToParentSize()
        add(scenarioTableView)
        add(scenarioDetailView)

        controller.displayOverlayProperty.addListener { _, _, display ->
            if (display) {
                replaceWith(overlayContainer)
                overlayContainer.children.add(0, this)
            } else {
                overlayContainer.replaceWith(this)
            }
        }
    }
}