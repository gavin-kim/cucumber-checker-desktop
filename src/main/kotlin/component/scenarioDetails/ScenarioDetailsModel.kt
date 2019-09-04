package component.scenarioDetails

import model.cucumber.Step

enum class ScenarioDetailsGroup(val text: String) {
    BEFORE_HOOKS("Before Hooks"),
    BACKGROUND_STEPS("Background Steps"),
    STEPS("Steps"),
    AFTER_HOOKS("After Hooks")
}

data class ScenarioDetails(
    val keyword: String,
    val name: String,
    val duration: String,
    val result: Step.Result,
    val messages: List<String>,
    val arguments: List<List<String>>
)