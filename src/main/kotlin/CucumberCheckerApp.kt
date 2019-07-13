import tornadofx.*
import component.main.MainView
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.reflect.KClass

class CucumberCheckerApp: App(MainView::class) {
    override val config: ConfigProperties
        get() = super.config
    override val configBasePath: Path
        get() = super.configBasePath
    override val configCharset: Charset
        get() = super.configCharset
    override val configPath: Path
        get() = super.configPath
    override val primaryView: KClass<out UIComponent>
        get() = super.primaryView
    override var scope: Scope
        get() = super.scope
        set(value) {}

    override fun createPrimaryScene(view: UIComponent): Scene {
        return Scene(view.root, 1600.0, 900.0, Color.WHITE)
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
    }

    override fun shouldShowPrimaryStage(): Boolean {
        return super.shouldShowPrimaryStage()
    }

    override fun start(stage: Stage) {
        super.start(stage)
    }

    override fun stop() {
        super.stop()
    }
}