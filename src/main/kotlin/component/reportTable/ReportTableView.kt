package component.reportTable

import model.Feature
import model.Scenario
import tornadofx.*

class ReportTableView: View("ReportTableView") {

    private val controller: ReportTableController by inject()

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