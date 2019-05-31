package view.buildSearchView

import event.DispatchReportEvent
import model.Job
import mu.KotlinLogging
import service.CucumberReportService
import tornadofx.*

class BuildSearchView: View("BuildSearchView") {

    private val logger = KotlinLogging.logger {}
    private val buildSearchViewModel: BuildSearchViewModel by inject()
    private val cucumberReportService: CucumberReportService by inject()

    override val root = vbox {
        label("Job")
        combobox(property = buildSearchViewModel.selectedJobProperty(), values = Job.values().toList())
            .setOnAction {
                val job = buildSearchViewModel.selectedJobProperty().value

                runAsyncWithOverlay {
                    cucumberReportService.getBuilds(job)
                } success  {
                    buildSearchViewModel.updateBuilds(it)
                } fail {
                    logger.error { it.message }
                }
            }


        label("Build")
        listview(buildSearchViewModel.buildListProperty()) {
            multiSelect(false)
            bindSelected(buildSearchViewModel.selectedBuildProperty())
        }

        button("search") {
            useMaxWidth = true

            action {
                val job = buildSearchViewModel.selectedJobProperty().value
                val build = buildSearchViewModel.selectedBuildProperty().value

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