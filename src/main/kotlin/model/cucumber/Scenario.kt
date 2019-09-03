package model.cucumber

data class Scenario(
    val tags: List<String>,
    val name: String,
    val hooks: List<Step>,
    val backgroundSteps: List<Step>,
    val steps: List<Step>,
    val screenShotFiles: List<String>,
    val unstable: Boolean
)