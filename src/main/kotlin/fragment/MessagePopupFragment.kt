package fragment

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import tornadofx.Fragment
import tornadofx.hbox
import tornadofx.stackpane
import tornadofx.text
import tornadofx.vbox

class MessagePopupFragment : Fragment() {

    val iconPath: String by param()
    val message: String by param()

    val onClose: EventHandler<ActionEvent> by param()
    val onOpen: EventHandler<ActionEvent> by param()

    val buttons: Collection<Button> by param()

    override val root = vbox(alignment = Pos.CENTER) {
        stackpane {
            text(message)
        }
        hbox(alignment = Pos.CENTER) {
            buttons.forEach { add(it) }
        }
    }
}