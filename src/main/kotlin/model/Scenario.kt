package model

data class Scenario(
    val tags: Set<String>,
    val name: String,
    val hooks: List<Step>,
    val steps: List<Step>,
    val screenShotFiles: List<String>,
    val unstable: Boolean
)