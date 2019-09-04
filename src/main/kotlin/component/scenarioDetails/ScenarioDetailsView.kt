package component.scenarioDetails

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.TreeItem
import javafx.scene.paint.Color
import model.cucumber.Step
import tornadofx.View
import tornadofx.c
import tornadofx.cellFormat
import tornadofx.div
import tornadofx.field
import tornadofx.fieldset
import tornadofx.fitToParentHeight
import tornadofx.fitToParentSize
import tornadofx.form
import tornadofx.gridpane
import tornadofx.hbox
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.populate
import tornadofx.row
import tornadofx.stackpane
import tornadofx.style
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.treeview
import tornadofx.vbox

class ScenarioDetailsView : View("ScenarioDetailsView") {

    private val controller: ScenarioDetailsController by inject()

    private val stepColorPassed = app.config.string("scenario.detail.step.color.passed", "#89DE9C")
    private val stepColorFailed = app.config.string("scenario.detail.step.color.failed", "#F8928D")
    private val stepColorSkipped = app.config.string("scenario.detail.step.color.skipped", "#84A8FA")
    private val stepColorUndefined = app.config.string("scenario.detail.step.color.undefined", "#F9BA7C")
    private val stepColorPending = app.config.string("scenario.detail.step.color.pending", "#F5F399")
    private val stepColorDefault = app.config.string("scenario.detail.step.color.default", "#D3D3D3")


    override val root = hbox(2) {
        style {
            fontFamily = "Consolas"
        }

        treeview<Any> {
            this.root = TreeItem("Details")

            prefWidthProperty().bind(this@hbox.widthProperty().div(2))

            cellFormat {
                graphic = vbox(2) {
                    val text = when (it) {
                        is String -> it
                        is ScenarioDetails -> "${it.keyword} ${it.name}"
                        is ScenarioDetailsGroup -> it.text
                        else -> throw IllegalArgumentException()
                    }

                    stackpane {
                        label(text)

                        style {
                            alignment = Pos.CENTER_LEFT
                        }
                    }

                    if (it is ScenarioDetails && it.arguments.isNotEmpty()) {
                        gridpane {
                            it.arguments.forEach { arguments ->
                                row {
                                    arguments.forEach { argument ->
                                        add(label(argument) {
                                            style {
                                                paddingAll = 3
                                            }
                                        })
                                    }
                                }
                            }

                            style {
                                gridLinesVisible = true
                                alignment = Pos.CENTER_LEFT
                            }
                        }
                    }
                }

                style {
                    if (it is ScenarioDetails) {
                        backgroundColor += getBackGroundColor(it.result)
                    }
                }
            }

            populate { parent ->
                when {
                    parent == root -> controller.scenarioGroupListProperty
                    parent.value == ScenarioDetailsGroup.BEFORE_HOOKS -> controller.beforeHookListProperty
                    parent.value == ScenarioDetailsGroup.BACKGROUND_STEPS -> controller.backgroundStepListProperty
                    parent.value == ScenarioDetailsGroup.STEPS -> controller.stepListProperty
                    parent.value == ScenarioDetailsGroup.AFTER_HOOKS -> controller.afterHookListProperty
                    else -> null
                }
            }

            controller.modelUpdatedProperty.onChange {
                expandFailedStepGroups(root)
                scrollTo(getFirstFailedStepIndex(root))
            }
        }

        form {
            prefWidthProperty().bind(this@hbox.widthProperty().div(2))

            fieldset(labelPosition = Orientation.VERTICAL) {
                field("Feature Tags") {
                    textfield(controller.featureTagsProperty) {
                        isEditable = false
                    }
                }

                field("Scenario Tags") {
                    textfield(controller.scenarioTagsProperty) {
                        isEditable = false
                    }
                }

                field("Messages") {
                    textarea(controller.errorMessageProperty) {
                        isEditable = false
                        fitToParentHeight()
                    }
                    fitToParentHeight()
                }
                fitToParentHeight()
            }
        }

    }

    private fun expandFailedStepGroups(root: TreeItem<Any>) {
        root.isExpanded = true
        root.children.forEach { stepGroupTreeItem ->
            if (stepGroupTreeItem.children.any { (it.value as ScenarioDetails).result == Step.Result.FAILED }) {
                stepGroupTreeItem.isExpanded = true
            }
        }
    }

    private fun getFirstFailedStepIndex(root: TreeItem<Any>): Int {
        root.children.forEachIndexed { groupIndex, group ->
            group.children.forEachIndexed {  stepIndex, step ->
                if ((step.value as ScenarioDetails).result == Step.Result.FAILED) {
                    return 1 + groupIndex + 1 + stepIndex
                }
            }
        }

        return 0
    }

    private fun getBackGroundColor(result: Step.Result): Color {
        return when (result) {
            Step.Result.PASSED -> c(stepColorPassed)
            Step.Result.FAILED -> c(stepColorFailed)
            Step.Result.SKIPPED -> c(stepColorSkipped)
            Step.Result.UNDEFINED -> c(stepColorUndefined)
            Step.Result.PENDING -> c(stepColorPending)
            else -> c(stepColorDefault)
        }
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}