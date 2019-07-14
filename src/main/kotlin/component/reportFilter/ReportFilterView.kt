package component.reportFilter

import component.scenarioTable.ScenarioTableColumn
import event.DispatchReportFilterData
import javafx.geometry.Orientation
import tornadofx.*

class ReportFilterView : View("ReportFilterView") {

    private val controller: ReportFilterController by inject()

    override val root = squeezebox {
        fold("Report Filter", expanded = false) {
            form {
                hbox(5) {
                    fieldset(labelPosition = Orientation.HORIZONTAL) {
                        usePrefWidth = true

                        field("Show Unstable Tests") {
                            checkbox(property = controller.showUnstableTestsProperty)
                        }

                        field("Display Columns") {
                            usePrefHeight = true
                            prefHeight = 150.0

                            listview(ScenarioTableColumn.values().toList().toObservable()) {
                                multiSelect(true)

                                selectionModel.select(ScenarioTableColumn.FEATURE)
                                selectionModel.select(ScenarioTableColumn.SCENARIO)
                                selectionModel.select(ScenarioTableColumn.FAILED_SPOT)
                                selectionModel.select(ScenarioTableColumn.SCREENSHOTS)
                                controller.selectedDisplayColumnsProperty.bindContent(selectionModel.selectedItems)


                                cellFormat {
                                    text = it.label
                                }
                            }
                        }
                    }

                    fieldset {

                    }
                }

                style {
                    fontSize = 10.px
                }
            }
        }
    }
}