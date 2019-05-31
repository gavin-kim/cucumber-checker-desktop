package view.reportView

import model.Feature
import model.Scenario
import tornadofx.*

class ReportView: View("ReportView") {

    private val reportViewModel: ReportViewModel by inject()

    override val root = tableview(reportViewModel.failureListProperty()) {
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