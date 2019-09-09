package model.cucumber

data class BriefReport(
    val build: Build,
    val reportUrl: String,
    val failedFeatureScenarioNames: List<Pair<String, String>>
)