package component.reportFilter

import component.scenarioTable.ScenarioTableColumn
import event.DispatchReportFilterData
import event.RequestReportFilterData
import javafx.beans.property.SimpleListProperty
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.*

class ReportFilterController : Controller() {

    private var showUnstableTests: Boolean by property(false)
    val showUnstableTestsProperty = getProperty(ReportFilterController::showUnstableTests)

    private val selectedDisplayColumns: ObservableList<ScenarioTableColumn> by listProperty(observableListOf())
    val selectedDisplayColumnsProperty = SimpleListProperty(selectedDisplayColumns)

    fun getReportFilterData(): ReportFilterData {
        return ReportFilterData(
            showUnstableTestsProperty.get(),
            selectedDisplayColumnsProperty.get()
        )
    }

    init {
        subscribe<RequestReportFilterData> {
            fire(DispatchReportFilterData(getReportFilterData()))
        }

        showUnstableTestsProperty.onChange { fire(DispatchReportFilterData(getReportFilterData())) }
        selectedDisplayColumnsProperty.onChange<ObservableList<ScenarioTableColumn>> { fire(DispatchReportFilterData(getReportFilterData())) }
    }
}