package fragment

import javafx.scene.image.Image
import tornadofx.Fragment
import tornadofx.fitToParentSize
import tornadofx.imageview
import tornadofx.stackpane

class ScreenShotFragment: Fragment() {

    val link: String by param()

    override val root = stackpane {

        val image = Image(link)

        imageview(image) {
            fitHeight = image.height
            fitWidth = image.width
            isPreserveRatio = true
            fitToParentSize()
        }
    }
}