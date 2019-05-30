package view

import controller.ReportViewController
import controller.SearchViewController
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
            combobox(property = searchViewController.selectedJobProperty(), values = Job.values().toList())
            hbox {
                label("Job Id")
                textfield(property = searchViewController.buildIdProperty())
            }
            button("search") {
                useMaxWidth = true

                action {
                    runAsync {
                        searchViewController.getCucumberReport()
                    } success {
                        logger.debug { "success: $it" }
                    } fail {
                        logger.debug { "success: $it" }
                    } finally {
                        logger.debug { "finally" }
                    }
                }
            }
        }

        center = treetableview<Scenario> {

        }
    }
}