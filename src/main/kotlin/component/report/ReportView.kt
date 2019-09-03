package component.report

import component.scenarioDetail.ScenarioDetailView
import component.scenarioTable.ScenarioTableView
import tornadofx.MaskPane
import tornadofx.View
import tornadofx.fitToParentSize
import tornadofx.replaceWith
import tornadofx.stackpane
import tornadofx.vbox

class ReportView : View("ReportView") {

    private val controller: ReportController by inject()
    private val overlayContainer = stackpane { add(MaskPane()) }

    override val root = vbox(2) {
        add(ScenarioTableView::class) {
            this.root.prefHeightProperty().bind(this@vbox.heightProperty().multiply(0.6))
        }
        add(ScenarioDetailView::class) {
            this.root.prefHeightProperty().bind(this@vbox.heightProperty().multiply(0.4))
        }

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