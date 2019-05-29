import tornadofx.App
import tornadofx.launch
import view.MainView

class CucumberCheckerApp: App(MainView::class)


fun main(args: Array<String>) {
    launch<CucumberCheckerApp>(args)
}
