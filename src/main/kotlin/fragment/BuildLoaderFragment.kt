package fragment

import component.buildLoader.BuildLoaderView
import tornadofx.Fragment
import tornadofx.stackpane
import tornadofx.usePrefSize

class BuildLoaderFragment : Fragment("Build Loader") {
    override val root = stackpane {

        usePrefSize = true
        prefWidth = 800.0
        prefHeight = 600.0

        add(BuildLoaderView::class)
    }
}