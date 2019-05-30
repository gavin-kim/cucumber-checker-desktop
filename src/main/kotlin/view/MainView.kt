package view

import controller.ReportViewController
import controller.SearchViewController
import model.Feature
import model.Job
import model.Scenario
import mu.KotlinLogging
import tornadofx.*

class MainView: View() {

    private val logger = KotlinLogging.logger {}
    private val searchViewController: SearchViewController by inject()
    private val reportViewController: ReportViewController by inject()

    override val root = borderpane {
        left = vbox {
            hbox {
                label("Job")
                combobox(property = searchViewController.selectedJobProperty(), values = Job.values().toList())
                    .setOnAction {
                        searchViewController.updateBuilds()
                    }
            }
            hbox {
                label("Build")
                combobox(property = searchViewController.selectedBuildProperty(), values = searchViewController.buildListProperty())
            }
            button("search") {
                useMaxWidth = true

                action {
                    val job = searchViewController.selectedJobProperty().value
                    val build = searchViewController.selectedBuildProperty().value

                    runAsync {
                        reportViewController.loadReport(job, build)
                    }
                }
            }
        }

        center = tableview(reportViewController.failureListProperty()) {
            readonlyColumn("Feature", Feature::name)
            readonlyColumn("Tags", Feature::tags)

            rowExpander(expandOnDoubleClick = true) {
                paddingLeft = expanderColumn.width

                tableview(it.failedScenarios.asObservable()) {
                    readonlyColumn("Scenario", Scenario::name)
                    readonlyColumn("Tags", Scenario::tags )
                    readonlyColumn("Failed Step", Scenario::failedStep)
                    readonlyColumn("Failed Reason", Scenario::failedReason)
                }
            }
        }
    }
}