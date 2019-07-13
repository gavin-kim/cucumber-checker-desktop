package component.scenarioDetail

import javafx.scene.control.TreeItem
import javafx.scene.paint.Color
import model.Result
import tornadofx.*


class ScenarioDetailView : View("ScenarioDetailView") {

    private val controller: ScenarioDetailController by inject()

    override val root = treeview<Any> {
        root = TreeItem("Details")

        cellFormat {
            text = when (it) {
                is String -> it
                is ScenarioDetailItem -> "${it.keyword} ${it.name}"
                is ScenarioDetailGroup -> it.text
                else -> throw IllegalArgumentException()
            }

            style {
                if (it is ScenarioDetailItem) {
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
            if (stepGroupTreeItem.children.any { (it.value as ScenarioDetailItem).result == Result.FAILED }) {
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

    override fun onDock() {
        root.fitToParentSize()
    }
}