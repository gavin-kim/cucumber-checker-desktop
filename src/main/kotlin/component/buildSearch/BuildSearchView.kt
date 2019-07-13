package component.buildSearch

import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import model.Build
import mu.KotlinLogging
import tornadofx.*

class BuildSearchView : View("BuildSearchView") {

    private val logger = KotlinLogging.logger {}
    private val controller: BuildSearchController by inject()

    override val root = vbox {
        label("Job")
        combobox(property = controller.selectedJobProperty, values = controller.jobListProperty) {
            useMaxWidth = true
            onAction = controller.onJobListChange(this)
        }

        label("Build")
        textfield(controller.buildFilterValueProperty)
        listview(controller.buildListProperty) {
            fitToParentHeight()
            multiSelect(false)
            bindSelected(property = controller.selectedBuildProperty)

            cellFormat {
                text =
                    if (it.userId.isBlank()) "${it.id}, ${it.name}"
                    else "${it.id}, ${it.name} (${it.userId}, ${it.userName})"

                val image = when (it.result) {
                    Build.Result.ABORTED -> Image("image/grey.png", 20.0, 20.0, true, true)
                    Build.Result.SUCCESS -> Image("image/blue.png", 20.0, 20.0, true, true)
                    Build.Result.UNSTABLE -> Image("image/yellow.png", 20.0, 20.0, true, true)
                    Build.Result.FAILURE -> Image("image/red.png", 20.0, 20.0, true, true)
                    Build.Result.UNKNOWN -> Image("", 20.0, 20.0, true, true)
                }

                graphic = imageview(image)
                isDisable = it.hasReport.not()

                style {
                    textFill = if (it.hasReport.not()) Color.DARKGRAY else Color.BLACK
                }
            }
        }

        button("Get Report") {
            useMaxWidth = true
            onAction = controller.onGetReportButtonClick(this)
        }
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}