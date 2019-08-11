package component.menuBar

import fragment.MessagePopupFragment
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import tornadofx.View
import tornadofx.imageview
import tornadofx.listmenu

class MenuBarView : View("MenuBarView") {

    private val barController: MenuBarController by inject()

    private val loadImagePath = app.config.string("menuBar.load.image.path")
    private val saveImagePath = app.config.string("menuBar.save.image.path")

    override val root = listmenu(orientation = Orientation.HORIZONTAL) {
        item(graphic = imageview(Image(loadImagePath, 24.0, 24.0, true, true))) {
            onMouseClicked = barController.onLoadReportButtonClicked
        }

        item(graphic = imageview(Image(saveImagePath, 24.0, 24.0, true, true))) {
            onMouseClicked = barController.onSaveReportButtonClicked
        }
        item("Test") {
            onMouseClicked = EventHandler<MouseEvent> {
                val button = Button()
                button.text = "button"

                find<MessagePopupFragment>(
                    MessagePopupFragment::message to "hi",
                    MessagePopupFragment::buttons to listOf(button)
                ).openModal()
            }
        }
    }
}