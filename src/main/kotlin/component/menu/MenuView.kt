package component.menu

import javafx.geometry.Orientation
import javafx.scene.image.Image
import tornadofx.View
import tornadofx.imageview
import tornadofx.listmenu

class MenuView : View("MenuView") {

    private val controller: MenuController by inject()

    private val loadImagePath = app.config.string("menu.load.image.path")
    private val saveImagePath = app.config.string("menu.save.image.path")

    override val root = listmenu(orientation = Orientation.HORIZONTAL) {
        item(graphic = imageview(Image(loadImagePath, 24.0, 24.0, true, true))) {
            onMouseClicked = controller.onLoadReportButtonClicked
        }

        item(graphic = imageview(Image(saveImagePath, 24.0, 24.0, true, true))) {
            onMouseClicked = controller.onSaveReportButtonClicked
        }
    }
}