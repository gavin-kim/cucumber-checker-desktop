package component.scenarioDetail

import javafx.geometry.Pos
import javafx.scene.control.TreeItem
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import model.Step
import tornadofx.*


class ScenarioDetailView : View("ScenarioDetailView") {

    private val controller: ScenarioDetailController by inject()

    override val root = treeview<Any> {
        root = TreeItem("Details")

        cellFormat {
            graphic = vbox {
                val text = when (it) {
                    is String -> it
                    is ScenarioDetail -> "${it.keyword} ${it.name}"
                    is ScenarioDetailGroup -> it.text
                    else -> throw IllegalArgumentException()
                }

                stackpane {
                    label(text)

                    style {
                        alignment = Pos.CENTER_LEFT
                    }
                }

                if (it is ScenarioDetail && it.arguments.isNotEmpty()) {
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
                if (it is ScenarioDetail) {
                    backgroundColor += getBackGroundColor(it.result)
                }
            }
        }

        populate { parent ->
            when {
                parent == root -> controller.scenarioGroupListProperty
                parent.value == ScenarioDetailGroup.BEFORE_HOOKS -> controller.beforeHookListProperty
                parent.value == ScenarioDetailGroup.BACKGROUND_STEPS -> controller.backgroundStepListProperty
                parent.value == ScenarioDetailGroup.STEPS -> controller.stepListProperty
                parent.value == ScenarioDetailGroup.AFTER_HOOKS -> controller.afterHookListProperty
                else -> null
            }
        }

        controller.updatedProperty.addListener { _, _, _ ->
            expandFailedStepGroups(root)
        }

    }

    private fun expandFailedStepGroups(root: TreeItem<Any>) {
        root.children.forEach { stepGroupTreeItem ->
            if (stepGroupTreeItem.children.any { (it.value as ScenarioDetail).result == Step.Result.FAILED }) {
                root.isExpanded = true
                stepGroupTreeItem.isExpanded = true
            }
        }
    }

    private fun getBackGroundColor(result: Step.Result): Color {
        return when (result) {
            Step.Result.PASSED -> c("#79FEAA")
            Step.Result.FAILED -> c("#FE7D7D")
            Step.Result.SKIPPED -> c("#68B8FE")
            else -> c("#DEF972")
        }
    }

    override fun onDock() {
        root.fitToParentSize()
    }
}