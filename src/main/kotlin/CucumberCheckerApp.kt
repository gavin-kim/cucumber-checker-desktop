import component.main.MainView
import javafx.scene.Scene
import javafx.scene.paint.Color
import mu.KotlinLogging
import tornadofx.App
import tornadofx.UIComponent
import java.nio.file.Path
import java.util.*

private const val APP_PROPERTIES_FILE = "app.properties"

class CucumberCheckerApp: App(MainView::class) {

    private val logger = KotlinLogging.logger {}

    override val configPath: Path = configBasePath.resolve("config.properties")

    override fun createPrimaryScene(view: UIComponent): Scene {
        return Scene(view.root, 1600.0, 900.0, Color.WHITE)
    }

    init {
        val properties = loadAppProperties()
        savePropertiesToConfig(properties)
    }

    private fun loadAppProperties(): Properties {
        return CucumberCheckerApp::class.java.getResourceAsStream(APP_PROPERTIES_FILE)
            .use { stream -> Properties().also { it.load(stream) } }
    }

    private fun savePropertiesToConfig(properties: Properties) {
        properties.entries.forEach { this.config[it.key] = it.value }
    }
}