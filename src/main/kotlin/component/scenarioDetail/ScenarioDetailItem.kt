package component.scenarioDetail

import model.Result

data class ScenarioDetailItem(
    val keyword: String,
    val name: String,
    val duration: String,
    val result: Result,
    val messages: List<String>,
    val arguments: List<List<String>>
)

