package component.scenarioTable

import event.RequestReportFilterData
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ScenarioTableView : View("ScenarioTableView") {

    private val controller: ScenarioTableController by inject()

    private val scenarioFailedImagePath = app.config.string("scenario.failed.image.path")
    private val scenarioUnstableImagePath = app.config.string("scenario.unstable.image.path")

    override val root = tableview(controller.scenarioRowTableRowListProperty) {
        bindSelected(controller.selectedReportRowProperty)

        readonlyColumn("", ScenarioTableRow::unstable) {
            usePrefWidth = true
            prefWidth = 24.0

            cellFormat {
                text = ""
                graphic = imageview(getStatusIconImage(it))
            }
        }
        readonlyColumn(ScenarioTableColumn.FEATURE.label, ScenarioTableRow::featureName) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.FEATURE, visibleProperty())
        }
        readonlyColumn(ScenarioTableColumn.FEATURE_TAGS.label, ScenarioTableRow::featureTags) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.FEATURE_TAGS, visibleProperty())
        }
        readonlyColumn(ScenarioTableColumn.SCENARIO.label, ScenarioTableRow::scenarioName) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.SCENARIO, visibleProperty())
        }
        readonlyColumn(ScenarioTableColumn.SCENARIO_TAGS.label, ScenarioTableRow::scenarioTags) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.SCENARIO_TAGS, visibleProperty())
        }
        readonlyColumn(ScenarioTableColumn.SCREENSHOTS.label, ScenarioTableRow::screenShotLinks) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.SCREENSHOTS, visibleProperty())

            cellCache {
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
        }
        readonlyColumn(ScenarioTableColumn.FAILED_SPOT.label, ScenarioTableRow::failedSpot) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.FAILED_SPOT, visibleProperty())
        }
        readonlyColumn(ScenarioTableColumn.FAILED_STEPS.label, ScenarioTableRow::failedSteps) {
            controller.setColumnVisibleProperty(ScenarioTableColumn.FAILED_STEPS, visibleProperty())
        }

        style {
            fontSize = 12.px
            padding = box(0.px)
        }
    }

    private fun getStatusIconImage(unstable: Boolean): Image {
        return Image(if (unstable) scenarioUnstableImagePath else scenarioFailedImagePath, 16.0, 16.0, true, true)
    }

    override fun onDock() {
        root.fitToParentSize()
        fire(RequestReportFilterData())
    }
}