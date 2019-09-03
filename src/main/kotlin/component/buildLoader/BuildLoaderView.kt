package component.buildLoader

import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.paint.Color
import model.cucumber.Build
import mu.KotlinLogging
import tornadofx.View
import tornadofx.bindChildren
import tornadofx.bindSelected
import tornadofx.button
import tornadofx.combobox
import tornadofx.field
import tornadofx.fieldset
import tornadofx.fitToParentHeight
import tornadofx.fitToParentSize
import tornadofx.form
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.listview
import tornadofx.multiSelect
import tornadofx.onChange
import tornadofx.scrollpane
import tornadofx.style
import tornadofx.textfield
import tornadofx.useMaxWidth
import tornadofx.vbox

class BuildLoaderView : View("BuildLoaderView") {

    private val logger = KotlinLogging.logger {}

    private val controller: BuildLoaderController by inject()

    private val buildImageSuccess = app.config.string("build.image.success")
    private val buildImageFailure = app.config.string("build.image.failure")
    private val buildImageUnstable = app.config.string("build.image.unstable")
    private val buildImageAborted = app.config.string("build.image.aborted")

    override val root = hbox(2) {
        vbox(2) {
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
                promptText = "Search Build"
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

            button("Load") {
                useMaxWidth = true
                onAction = controller.onGetReportButtonClick(this@BuildLoaderView)
            }

            fitToParentSize()
        }

        scrollpane(fitToWidth = true, fitToHeight = true) {
            form {
                fieldset(labelPosition = Orientation.VERTICAL) {
                    bindChildren(controller.buildParameterMapProperty) { name, value ->
                        field(name) {
                            textfield(value) {
                                isEditable = false
                            }
                        }
                    }
                    controller.buildParameterResetProperty.onChange { children.clear() }
                }
            }
            fitToParentSize()
        }
    }

    private fun getImage(result: Build.Result): Image {
        val imagePath = when (result) {
            Build.Result.ABORTED -> buildImageAborted
            Build.Result.SUCCESS -> buildImageSuccess
            Build.Result.UNSTABLE -> buildImageUnstable
            Build.Result.FAILURE -> buildImageFailure
            Build.Result.UNKNOWN -> ""
        }

        return Image(imagePath, 20.0, 20.0, true, true)
    }

    override fun onDock() {
        root.fitToParentHeight()
    }
}