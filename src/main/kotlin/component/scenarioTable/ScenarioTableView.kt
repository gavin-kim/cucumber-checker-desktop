package component.scenarioTable

import event.RequestReportFilterData
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.View
import tornadofx.bindSelected
import tornadofx.box
import tornadofx.fitToParentSize
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.px
import tornadofx.readonlyColumn
import tornadofx.style
import tornadofx.tableview
import tornadofx.usePrefWidth

class ScenarioTableView : View("ScenarioTableView") {

    private val controller: ScenarioTableController by inject()

    private val failedImagePath = app.config.string("scenario.table.failed.image.path")
    private val unstableImagePath = app.config.string("scenario.table.unstable.image.path")

    override val root = tableview(controller.scenarioRowTableRowListProperty) {
        bindSelected(controller.selectedReportRowProperty)
        selectionModel.isCellSelectionEnabled = true

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

            cellFormat {
                graphic = hbox {
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

        onKeyPressed = controller.onKeyPressed
    }

    private fun getStatusIconImage(unstable: Boolean): Image {
        return Image(if (unstable) unstableImagePath else failedImagePath, 16.0, 16.0, true, true)
    }

    override fun onDock() {
        root.fitToParentSize()
        fire(RequestReportFilterData())
    }
}