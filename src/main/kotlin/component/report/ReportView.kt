package component.report

import component.scenarioDetail.ScenarioDetailView
import component.scenarioTable.ScenarioTableView
import tornadofx.View
import tornadofx.vbox

class ReportView : View("ReportView") {

    private val scenarioTableView: ScenarioTableView by inject()
    private val scenarioDetailView: ScenarioDetailView by inject()

    override val root = vbox {
        add(scenarioTableView)
        add(scenarioDetailView)
    }
}