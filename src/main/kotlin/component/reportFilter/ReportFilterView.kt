package component.reportFilter

import tornadofx.*

class ReportFilterView : View("ReportFilterView") {

    private val controller: ReportFilterController by inject()

    override val root = squeezebox {
        fold("Filter", expanded = false) {
            form {

            }
        }
    }
}