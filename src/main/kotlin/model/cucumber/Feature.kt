package model.cucumber

data class Feature(
    val name: String,
    val tags: List<String>,
    val failedScenarios: List<Scenario>
)