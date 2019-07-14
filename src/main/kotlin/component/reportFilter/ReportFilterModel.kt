package component.reportFilter

import component.scenarioTable.ScenarioTableColumn

data class ReportFilterData(
    val showUnstableTests: Boolean,
    val displayColumns: List<ScenarioTableColumn>
)