package component.scenarioTable

enum class ScenarioTableColumn(val label: String) {
    FEATURE("Feature"),
    FEATURE_TAGS("Feature Tags"),
    SCENARIO("Scenario"),
    SCENARIO_TAGS("Scenario Tags"),
    SCREENSHOTS("Screenshots"),
    FAILED_SPOT("Failed Spot"),
    FAILED_STEP("Failed Step")
}

data class ScenarioTableRow(
    val featureName: String,
    val featureTags: String,
    val scenarioName: String,
    val scenarioTags: String,
    val screenShotLinks: List<String>,
    val failedSpot: String,
    val failedStep: String,
    val unstable: Boolean
)