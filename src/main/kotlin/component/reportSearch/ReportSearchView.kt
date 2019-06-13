package component.reportSearch

import tornadofx.*

class ReportSearchView: View("ReportSearchView") {

    private val controller: ReportSearchView by inject()

    override val root = form {
        hbox(20) {
            fieldset("Feature") {
                hbox(20) {
                    vbox {
                        textfield("Name") {  }
                    }
                }
            }
            fieldset("Scenario") {
                hbox(20) {

                }
            }
        }
    }
}