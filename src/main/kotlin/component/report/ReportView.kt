package component.report

import component.reportFilter.ReportFilterView
import component.scenarioDetail.ScenarioDetailView
import component.scenarioTable.ScenarioTableView
import tornadofx.*

class ReportView : View("ReportView") {

    private val controller: ReportController by inject()
    private val overlayContainer = stackpane { add(MaskPane()) }

    override val root = vbox(2) {
        add(ReportFilterView::class)
        add(ScenarioTableView::class)
        add(ScenarioDetailView::class)

        controller.displayOverlayProperty.addListener { _, _, display ->
            if (display) {
                replaceWith(overlayContainer)
                overlayContainer.children.add(0, this)
            } else {
                overlayContainer.replaceWith(this)
            }
        }
    }

    override fun onDock() {
        root.fitToParentSize()
        overlayContainer.fitToParentSize()
    }
}