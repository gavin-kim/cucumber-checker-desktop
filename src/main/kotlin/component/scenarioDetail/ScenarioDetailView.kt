package component.scenarioDetail

import javafx.scene.control.TreeItem
import javafx.scene.paint.Color
import model.Result
import tornadofx.*


class ScenarioDetailView : View("ScenarioDetailView") {

    private val controller: ScenarioDetailController by inject()

    override val root = treeview<Any> {
        fitToParentSize()

        root = TreeItem("Details")

        cellFormat {
            text = when (it) {
                is String -> it
                is StepDetail -> "${it.keyword} ${it.name}"
                is StepDetailGroup -> it.text
                else -> throw IllegalArgumentException()
            }

            style {
                if (it is StepDetail) {
                    backgroundColor += getBackGroundColor(it.result)
                }
            }
        }

        populate { parent ->
            when {
                parent == root -> controller.stepGroupsProperty
                parent.value == StepDetailGroup.BEFORE_HOOKS -> controller.beforeHooksProperty
                parent.value == StepDetailGroup.BACKGROUND_STEPS -> controller.backgroundStepsProperty
                parent.value == StepDetailGroup.STEPS -> controller.stepsProperty
                parent.value == StepDetailGroup.AFTER_HOOKS -> controller.afterHooksProperty
                else -> null
            }
        }

        controller.updatedProperty.addListener { _, _, _ ->
            expandFailedStepGroups(root)
        }

    }

    private fun expandFailedStepGroups(root: TreeItem<Any>) {
        root.children.forEach { stepGroupTreeItem ->
            if (stepGroupTreeItem.children.any { (it.value as StepDetail).result == Result.FAILED }) {
                root.isExpanded = true
                stepGroupTreeItem.isExpanded = true
            }
        }
    }

    private fun getBackGroundColor(result: Result): Color {
        return when (result) {
            Result.PASSED -> c("#79FEAA")
            Result.FAILED -> c("#FE7D7D")
            Result.SKIPPED -> c("#68B8FE")
            else -> c("#DEF972")
        }
    }
}