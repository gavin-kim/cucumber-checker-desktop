package component.scenarioDetail

import model.Step

enum class ScenarioDetailGroup(val text: String) {
    BEFORE_HOOKS("Before Hooks"),
    BACKGROUND_STEPS("Background Steps"),
    STEPS("Steps"),
    AFTER_HOOKS("After Hooks")
}

data class ScenarioDetail(
    val keyword: String,
    val name: String,
    val duration: String,
    val result: Step.Result,
    val messages: List<String>,
    val arguments: List<List<String>>
)