package component.scenarioTable

import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.SmartResize
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

    private val imageFailed = app.config.string("scenario.table.image.failed")
    private val imageUnstable = app.config.string("scenario.table.image.unstable")

    override val root = tableview<ScenarioTableRow> {
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
        readonlyColumn(ScenarioTableColumn.FEATURE.label, ScenarioTableRow::featureName)
        readonlyColumn(ScenarioTableColumn.SCENARIO.label, ScenarioTableRow::scenarioName)
        readonlyColumn(ScenarioTableColumn.SCREENSHOTS.label, ScenarioTableRow::screenShotLinks) {
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
        readonlyColumn(ScenarioTableColumn.FAILED_STEP.label, ScenarioTableRow::failedStep)

        style {
            fontSize = 12.px
            padding = box(0.px)
        }

        onKeyPressed = controller.onKeyPressed
        columnResizePolicy = SmartResize.POLICY
    }

    private fun getStatusIconImage(unstable: Boolean): Image {
        return Image(if (unstable) imageUnstable else imageFailed, 16.0, 16.0, true, true)
    }

    override fun onDock() {
        root.fitToParentSize()
        controller.filteredScenarioTableRowList.bindTo(root)
    }
}