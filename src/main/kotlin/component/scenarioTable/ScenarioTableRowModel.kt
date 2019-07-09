package component.scenarioTable

data class ScenarioTableRowModel(
    val featureName: String,
    val featureTags: String,
    val scenarioName: String,
    val scenarioTags: String,
    val screenShotLinks: List<String>,
    val failedSpot: String,
    val failedStep: String,
    val failedHooks: String
)