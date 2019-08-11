package fragment

import javafx.scene.image.Image
import model.Report
import tornadofx.Fragment
import tornadofx.fitToParentSize
import tornadofx.imageview
import tornadofx.stackpane
import java.io.File

class ScreenShotFragment : Fragment() {

    val reportType: Report.Type by param()
    val link: String by param()

    override val root = stackpane {

        val image =
            if (reportType == Report.Type.WEB) Image(link)
            else Image(File(link).inputStream())

        imageview(image) {
            fitHeight = image.height
            fitWidth = image.width
            isPreserveRatio = true
            fitToParentSize()
        }
    }
}