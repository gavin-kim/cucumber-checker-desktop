package component.scenarioTable

import model.cucumber.Scenario

enum class ScenarioTableColumn(val label: String) {
    FEATURE("Feature"),
    SCENARIO("Scenario"),
    SCREENSHOTS("Screenshots"),
    FAILED_STEP("Failed Step")
}

data class ScenarioTableRow(
    val scenario: Scenario,
    val featureName: String,
    val featureTags: List<String>,
    val scenarioName: String,
    val scenarioTags: List<String>,
    val screenShotLinks: List<String>,
    val failedStep: String,
    val messages: List<String>,
    val unstable: Boolean
)

data class ScenarioTableFilter(
    val showUnstableTests: Boolean
)