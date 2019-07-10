package component.scenarioDetail

import model.Hook
import model.Step

data class ScenarioDetailModel(
    val beforeHooks: List<Hook>,
    val backgroundSteps: List<Step>,
    val steps: List<Step>,
    val afterHooks: List<Hook>
)