package component.buildSearch

import javafx.scene.image.Image
import javafx.scene.paint.Color
import model.Build
import mu.KotlinLogging
import tornadofx.*

class BuildSearchView : View("BuildSearchView") {

    private val logger = KotlinLogging.logger {}
    private val controller: BuildSearchController by inject()

    override val root = vbox {

        label("Job") {
            padding = insets(5)
        }
        combobox(property = controller.selectedJobProperty, values = controller.jobListProperty) {
            useMaxWidth = true
            onAction = controller.onJobListChange(this)
        }

        label("Build") {
            padding = insets(5)
        }
        textfield(controller.buildFilterValueProperty) {
            promptText = "Build Filter"
        }
        listview(controller.buildListProperty) {
            fitToParentHeight()
            multiSelect(false)
            bindSelected(property = controller.selectedBuildProperty)

            cellFormat {
                text =
                    if (it.userId.isBlank()) "${it.id}, ${it.name}"
                    else "${it.id}, ${it.name} (${it.userId}, ${it.userName})"

                graphic = imageview(getImage(it.result))
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

    private fun getImage(result: Build.Result): Image {
        val imageFile = when (result) {
            Build.Result.ABORTED -> "image/grey.png"
            Build.Result.SUCCESS -> "image/blue.png"
            Build.Result.UNSTABLE -> "image/yellow.png"
            Build.Result.FAILURE -> "image/red.png"
            Build.Result.UNKNOWN -> ""
        }

        return Image(imageFile, 20.0, 20.0, true, true)
    }

    override fun onDock() {
        root.fitToParentHeight()
        root.usePrefWidth = true
        root.prefWidthProperty().set(400.0)
    }
}