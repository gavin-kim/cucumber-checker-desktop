package component.scenarioDetail

import model.Result

data class StepDetail(
    val keyword: String,
    val name: String,
    val duration: String,
    val result: Result,
    val messages: List<String>,
    val arguments: List<List<String>>
)

