package component.buildSearch

import event.DispatchReportEvent
import javafx.scene.image.Image
import model.Build
import model.Job
import mu.KotlinLogging
import service.CucumberReportService
import tornadofx.*

class BuildSearchView : View("BuildSearchView") {

    private val logger = KotlinLogging.logger {}

    private val jobs = Job.values().toList()
    private val controller: BuildSearchViewController by inject()
    private val cucumberReportService: CucumberReportService by inject()

    override val root = vbox {
        label("Job")
        combobox(property = controller.selectedJobProperty, values = jobs) {
            useMaxWidth =  true

            setOnAction {
                val job = controller.selectedJobProperty.value

                runAsyncWithOverlay {
                    cucumberReportService.getBuilds(job)
                } success {
                    controller.updateBuilds(it)
                } fail {
                    controller.updateBuilds(emptyList())
                    logger.error { it.message }
                }
            }
        }

        label("Build")
        textfield(controller.buildSearchBarProperty).textProperty()
        listview(controller.filteredBuildListProperty) {
            fitToParentHeight()
            multiSelect(false)
            bindSelected(property = controller.selectedBuildProperty)

            cellFormat {
                text = it.name

                val image = when (it.status) {
                    Build.Status.ABORTED -> Image("image/grey.png", 20.0, 20.0, true, true)
                    Build.Status.STABLE -> Image("image/blue.png", 20.0, 20.0, true, true)
                    Build.Status.UNSTABLE -> Image("image/yellow.png", 20.0, 20.0, true, true)
                    Build.Status.BROKEN -> Image("image/red.png", 20.0, 20.0, true, true)
                }

                graphic = imageview(image)
            }
        }

        button("Get Report") {
            useMaxWidth = true

            action {
                val job = controller.selectedJobProperty.value
                val build = controller.selectedBuildProperty.value

                runAsync {
                    cucumberReportService.getReport(job, build)
                } success {
                    fire(DispatchReportEvent(it))
                } fail {
                    logger.error { it.message }
                }
            }
        }
    }
}