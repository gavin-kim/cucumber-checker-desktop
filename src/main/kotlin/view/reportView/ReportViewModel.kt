package view.reportView

import event.DispatchReportEvent
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.Feature

import tornadofx.*

class ReportViewModel: Controller() {

    init {
        subscribe<DispatchReportEvent> {
            failureList.setAll(it.report.failedFeatures)
        }
    }

    private var failureList: ObservableList<Feature> by listProperty(mutableListOf<Feature>().asObservable())
    fun failureListProperty() = SimpleListProperty(failureList)
}