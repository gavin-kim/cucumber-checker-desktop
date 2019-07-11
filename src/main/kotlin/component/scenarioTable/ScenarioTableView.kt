package component.scenarioTable

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ScenarioTableView : View("ScenarioTableView") {

    private val controller: ScenarioTableController by inject()

    override val root = tableview(controller.reportRowModelListProperty) {
        bindSelected(controller.selectedReportRowProperty)
        smartResize()
        fitToParentSize()

        readonlyColumn("Feature", ScenarioTableRow::featureName)
        readonlyColumn("Feature Tags", ScenarioTableRow::featureTags)
        readonlyColumn("Scenario", ScenarioTableRow::scenarioName)
        readonlyColumn("Scenario Tags", ScenarioTableRow::scenarioTags)
        readonlyColumn("Screenshots", ScenarioTableRow::screenShotLinks).cellCache {
            hbox {
                it.mapIndexed { index, link ->
                    hyperlink("${index + 1}") {
                        onAction = controller.onScreenShotLinkClick(link)

                        style {
                            textFill = Color.CHOCOLATE
                            fontWeight = FontWeight.BOLD
                        }
                    }
                }
            }
        }
        readonlyColumn("Failed Spot", ScenarioTableRow::failedSpot)
        readonlyColumn("Failed Step", ScenarioTableRow::failedSteps)

        style {
            fontSize = 11.px
            padding = box(0.px)
        }
    }
}