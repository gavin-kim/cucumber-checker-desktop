package component.reportTable

import fragment.ScreenShotFragment
import javafx.stage.Modality
import javafx.stage.StageStyle
import model.Feature
import model.Scenario
import tornadofx.*

class ReportTableView: View("ReportTableView") {

    private val controller: ReportTableController by inject()

    override val root = tableview(controller.failureListProperty) {
        readonlyColumn("Feature", Feature::name)
        readonlyColumn("Tags", Feature::tags)

        rowExpander(expandOnDoubleClick = true) { feature ->
            label("asdf")
            paddingLeft = expanderColumn.width

            tableview(feature.failedScenarios.asObservable()) {
                readonlyColumn("Scenario", Scenario::name)
                readonlyColumn("Tags", Scenario::tags)
                readonlyColumn("ScreenShots", Scenario::screenShotFiles) {
                    cellCache {
                        hbox {
                            controller.buildScreenShotLinks(it).mapIndexed { index, link ->
                                hyperlink("${index + 1}") {
                                    setOnAction {
                                        find<ScreenShotFragment>(ScreenShotFragment::link to link)
                                            .openModal(StageStyle.UNDECORATED, Modality.NONE, resizable = true)
                                    }
                                }
                            }
                        }
                    }
                }

/*                rowExpander(expandOnDoubleClick = true) { scenario ->
                    paddingLeft = expanderColumn.width

                    val (beforeHooks, afterHooks) = scenario.hooks.partition { hook -> hook.type == Hook.Type.BEFORE }


                }*/
            }
        }
    }
}