package component.scenarioTable

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ScenarioTableView : View("ScenarioTableView") {

    private val controller: ScenarioTableController by inject()
    private val overlayContainer = stackpane { add(MaskPane()) }

    override val root = tableview(controller.reportRowModelListProperty) {
        smartResize()
        bindSelected(controller.selectedReportRowProperty)

        readonlyColumn("Feature", ScenarioTableRowModel::featureName)
        readonlyColumn("Feature Tags", ScenarioTableRowModel::featureTags)
        readonlyColumn("Scenario", ScenarioTableRowModel::scenarioName)
        readonlyColumn("Scenario Tags", ScenarioTableRowModel::scenarioTags)
        readonlyColumn("Screenshots", ScenarioTableRowModel::screenShotLinks).cellCache {
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
        readonlyColumn("Failed Spot", ScenarioTableRowModel::failedSpot)
        readonlyColumn("Failed Step", ScenarioTableRowModel::failedStep)
        readonlyColumn("Failed Hooks", ScenarioTableRowModel::failedHooks)

        controller.displayOverlayProperty.addListener { _, _, display ->
            if (display) {
                replaceWith(overlayContainer)
                overlayContainer.children.add(0, this)
            } else {
                overlayContainer.replaceWith(this)
            }
        }

        style {
            fontSize = 11.px
            padding = box(0.px)
        }
    }
}