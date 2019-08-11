package component.statusBar

import tornadofx.View
import tornadofx.hbox

class StatusBarView : View("Status View") {

    private val controller: StatusBarController by inject()

    override val root = hbox {

    }
}