package component.scenarioTable

import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ScenarioTableView : View("ScenarioTableView") {

    private val controller: ScenarioTableController by inject()

    override val root = tableview(controller.reportRowModelListProperty) {
        bindSelected(controller.selectedReportRowProperty)

        readonlyColumn("", ScenarioTableRow::unstable).cellFormat {
            text = ""
            graphic = imageview(Image(if (it) "image/warning.png" else "image/error.png", 20.0, 20.0, true, true))
        }
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

    override fun onDock() {
        root.fitToParentSize()
    }
}