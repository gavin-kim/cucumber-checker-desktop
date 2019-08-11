package component.buildSearch

import javafx.scene.image.Image
import javafx.scene.paint.Color
import model.Build
import mu.KotlinLogging
import tornadofx.View
import tornadofx.bindSelected
import tornadofx.button
import tornadofx.combobox
import tornadofx.fitToParentHeight
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.listview
import tornadofx.multiSelect
import tornadofx.style
import tornadofx.textfield
import tornadofx.useMaxWidth
import tornadofx.usePrefWidth
import tornadofx.vbox

class BuildSearchView : View("BuildSearchView") {

    private val logger = KotlinLogging.logger {}

    private val controller: BuildSearchController by inject()

    private val buildSuccessImagePath = app.config.string("build.success.image.path")
    private val buildFailureImagePath = app.config.string("build.failure.image.path")
    private val buildUnstableImagePath = app.config.string("build.unstable.image.path")
    private val buildAbortedImagePath = app.config.string("build.aborted.image.path")

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
                text = it.text
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
        val imagePath = when (result) {
            Build.Result.ABORTED -> buildAbortedImagePath
            Build.Result.SUCCESS -> buildSuccessImagePath
            Build.Result.UNSTABLE -> buildUnstableImagePath
            Build.Result.FAILURE -> buildFailureImagePath
            Build.Result.UNKNOWN -> ""
        }

        return Image(imagePath, 20.0, 20.0, true, true)
    }

    override fun onDock() {
        root.fitToParentHeight()
        root.usePrefWidth = true
        root.prefWidthProperty().set(400.0)
    }
}