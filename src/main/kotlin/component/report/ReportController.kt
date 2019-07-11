package component.report

import event.ReportButtonClicked
import event.ReportDisplayed
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property

class ReportController : Controller() {

    private var displayOverlay: Boolean by property()
    val displayOverlayProperty = getProperty(ReportController::displayOverlay)


    init {
        subscribe<ReportButtonClicked> {
            displayOverlay = true
        }

        subscribe<ReportDisplayed> {
            displayOverlay = false
        }
    }
}