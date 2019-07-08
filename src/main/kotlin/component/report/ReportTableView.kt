package component.report

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ReportTableView : View("ReportTableView") {

    private val controller: ReportTableController by inject()

    override val root = tableview(controller.failureListProperty) {

        smartResize()

        readonlyColumn("Feature", FeatureViewModel::featureName)
        readonlyColumn("Feature Tags", FeatureViewModel::featureTags)
        readonlyColumn("Scenario", FeatureViewModel::scenarioName)
        readonlyColumn("Scenario Tags", FeatureViewModel::scenarioTags)
        readonlyColumn("Screenshots", FeatureViewModel::screenShotLinks).cellCache {
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
        readonlyColumn("Failed Spot", FeatureViewModel::failedSpot)
        readonlyColumn("Failed Step", FeatureViewModel::failedStep).cellFormat {
            text = it
            tooltip("asdf")
        }
        readonlyColumn("Failed Hooks", FeatureViewModel::failedHooks)

        style {
            fontSize = 11.px
            padding = box(0.px)
            cellHeight = 14.px
        }
    }
}