package component.scenarioDetail

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.hbox
import tornadofx.label
import tornadofx.tableview

class ScenarioDetailView : View("ScenarioDetailView") {

    private val controller: ScenarioDetailController by inject()

    override val root = hbox(10, Pos.CENTER) {
        controller.scenarioProperty.addListener { observable, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                add(label(newValue.name))
            }
        }
    }
}