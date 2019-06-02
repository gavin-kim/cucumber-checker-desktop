package component.report

import model.Feature
import model.Scenario
import tornadofx.*

class ReportView: View("ReportView") {

    private val controller: ReportViewController by inject()

    override val root = tableview(controller.failureListProperty) {
        readonlyColumn("Feature", Feature::name)
        readonlyColumn("Tags", Feature::tags)

        rowExpander(expandOnDoubleClick = true) {
            paddingLeft = expanderColumn.width

            tableview(it.failedScenarios.asObservable()) {
                readonlyColumn("Scenario", Scenario::name)
                readonlyColumn("Tags", Scenario::tags )
                readonlyColumn("Failed Step", Scenario::failedStep)
                readonlyColumn("Failed Reason", Scenario::failedReason)
            }
        }
    }
}