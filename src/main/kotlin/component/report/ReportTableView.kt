package component.report

import tornadofx.*

class ReportTableView : View("ReportTableView") {

    private val controller: ReportTableController by inject()

    override val root = tableview(controller.failureListProperty) {
        smartResize()

        readonlyColumn("Feature", FeatureViewModel::featureName)
        readonlyColumn("Feature Tags", FeatureViewModel::featureTags)
        readonlyColumn("Scenario", FeatureViewModel::scenarioName)
        readonlyColumn("Scenario Tags", FeatureViewModel::scenarioTags)
        readonlyColumn("Screenshots", FeatureViewModel::screenShotLinks).cellCache {
            hbox {
                it.mapIndexed { index, link ->
                    hyperlink("${index + 1}") { onAction = controller.onScreenShotLinkClick(link) }
                }
            }
        }
        readonlyColumn("Failed Spot", FeatureViewModel::failedSpot)
        readonlyColumn("Failed Step", FeatureViewModel::failedStep)
        readonlyColumn("Failed Hooks", FeatureViewModel::failedHooks)
    }
}