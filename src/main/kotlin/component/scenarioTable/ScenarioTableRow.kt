package component.scenarioTable

data class ScenarioTableRow(
    val featureName: String,
    val featureTags: String,
    val scenarioName: String,
    val scenarioTags: String,
    val screenShotLinks: List<String>,
    val failedSpot: String,
    val failedSteps: String,
    val unstable: Boolean
)