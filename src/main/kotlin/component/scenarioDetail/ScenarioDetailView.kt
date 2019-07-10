package component.scenarioDetail

import javafx.scene.paint.Color
import model.Result
import tornadofx.*


class ScenarioDetailView : View("ScenarioDetailView") {

    private val controller: ScenarioDetailController by inject()

    override val root = tableview(controller.scenarioDetailModelProperty) {
        smartResize()

        readonlyColumn("Before Hooks", ScenarioDetailModel::beforeHooks).cellFormat {
            graphic = listview(it.toObservable()) {
                cellFormat { hook ->
                    text = hook.name

                    style {
                        backgroundColor += getBackGroundColor(hook.result)
                    }
                }
            }

        }

        readonlyColumn("Background Steps", ScenarioDetailModel::backgroundSteps).cellFormat {
            graphic = listview(it.toObservable()) {
                cellFormat { step ->
                    text = step.name

                    style {
                        backgroundColor += getBackGroundColor(step.result)
                    }
                }
            }

        }

        readonlyColumn("Steps", ScenarioDetailModel::steps).cellFormat {
            graphic = listview(it.toObservable()) {
                cellFormat { step ->
                    text = step.name

                    style {
                        backgroundColor += getBackGroundColor(step.result)
                    }
                }
            }
        }

        readonlyColumn("After Hooks", ScenarioDetailModel::afterHooks).cellFormat {
            graphic = listview(it.toObservable()) {
                cellFormat { hook ->
                    text = hook.name

                    style {
                        backgroundColor += getBackGroundColor(hook.result)
                    }
                }
            }
        }
    }

    private fun getBackGroundColor(result: Result): Color {
        return when (result) {
            Result.PASSED -> c("#79FEAA")
            Result.FAILED -> c("#FE7D7D")
            Result.SKIPPED -> c("#798BFE")
            else -> c("#DEF972")
        }
    }
}