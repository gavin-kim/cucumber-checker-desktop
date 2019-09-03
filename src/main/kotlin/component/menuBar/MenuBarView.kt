package component.menuBar

import fragment.BuildLoaderFragment
import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.paddingAll

class MenuBarView : View("MenuBarView") {

    private val controller: MenuBarController by inject()

    private val imageSearch = app.config.string("menu.bar.image.search")
    private val imageLoad = app.config.string("menu.bar.image.load")
    private val imageSave = app.config.string("menu.bar.image.save")
    private val imageCompare = app.config.string("menu.bar.image.compare")

    override val root = hbox(5, Pos.CENTER_LEFT) {
        button {
            graphic = imageview(Image(imageSearch, 24.0, 24.0, true, true))
            action { find<BuildLoaderFragment>().openModal() }
        }

        button {
            graphic = imageview(Image(imageLoad, 24.0, 24.0, true, true))
            onAction = controller.onLoadReportButtonClicked
        }

        button {
            graphic = imageview(Image(imageSave, 24.0, 24.0, true, true))
            onAction = controller.onSaveReportButtonClicked
        }

        button {
            graphic = imageview(Image(imageCompare, 24.0, 24.0, true, true))
            onAction = controller.onCompareButtonClicked
        }

        hbox(5, Pos.CENTER_RIGHT) {
            fitToParentWidth()
            checkbox("Show Unstable Failures", controller.showUnstableProperty)
        }

        paddingAll = 5
    }

    override fun onDock() {
        root.fitToParentWidth()
    }
}