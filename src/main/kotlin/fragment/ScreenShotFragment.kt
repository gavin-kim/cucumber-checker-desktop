package fragment

import tornadofx.*

class ScreenShotFragment: Fragment() {

    val link: String by param()

    override val root = stackpane {
        val image = resources.image(link)

        imageview(image) {
            fitHeight = image.height
            fitWidth = image.width
            isPreserveRatio = true
        }
    }
}