package component.report

import event.HideReportOverlay
import event.ShowReportOverlay
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property

class ReportController : Controller() {

    private var displayOverlay: Boolean by property()
    val displayOverlayProperty = getProperty(ReportController::displayOverlay)

    init {
        subscribe<ShowReportOverlay> {
            displayOverlay = true
        }

        subscribe<HideReportOverlay> {
            displayOverlay = false
        }
    }
}