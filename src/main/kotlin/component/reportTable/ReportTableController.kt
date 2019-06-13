package component.reportTable

import event.DispatchReportEvent
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Feature

import tornadofx.*

class ReportTableController: Controller() {

    init {
        subscribe<DispatchReportEvent> {
            failureList.setAll(it.report.failedFeatures)
        }
    }

    private var failureList: ObservableList<Feature> by listProperty(mutableListOf<Feature>().asObservable())
    val failureListProperty = SimpleListProperty(failureList)
}