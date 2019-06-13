package model

enum class Part {
    BEFORE_HOOKS,
    AFTER_HOOKS,
    STEPS,
    BACKGROUND
}

enum class Result(val cssClass: String) {
    PASSED("passed"),
    FAILED("failed"),
    UNDEFINED("undefined"),
    SKIPPED("skipped"),
    UNKNOWN("")
}

data class Hook(
    val type: Type,
    val name: String,
    val duration: String,
    val result: Result,
    val message: String? = null
) {
    enum class Type(val text: String) {
        BEFORE("Before"),
        AFTER("After"),
        UNKNOWN("")
    }
}

data class Step(
    val type: Type,
    val name: String,
    val duration: String,
    val result: Result,
    val messages: List<String>,
    val arguments: List<List<String>>
) {
    enum class Type(val text: String) {
        GIVEN("Given"),
        AND("And"),
        WHEN("When"),
        THEN("Then"),
        UNKNOWN("")
    }
}

data class CucumberReport(
    val job: Job,
    val build: Build,
    val failedFeatures: List<Feature>
)

data class Feature(
    val name: String,
    val tags: Set<String>,
    val failedScenarios: List<Scenario>,
    val backgroundSteps: List<Step>
)

data class Scenario(
    val tags: Set<String>,
    val name: String,
    val hooks: List<Hook>,
    val steps: List<Step>,
    val screenShotLinks: List<String>
)

data class Build(
    val id: Int,
    val name: String,
    val link: String,
    val status: Status
) {
    enum class Status {
        STABLE,
        UNSTABLE,
        ABORTED,
        BROKEN,
    }
}

enum class Job(val id: Int, val jobName: String) {
    MANUAL_ORACLE_JOB(1, "ExecuteCucumberRun-Oracle-Parallel"),
    MANUAL_SQL_SERVER_JOB(2, "ExecuteCucumberRun--SQLServer-Sequential"),
    BASIC_STEPS_ASV_SQL_SERVER(3, "rCucumber-BasicSteps-ASV-SQLServer-AutoTriggeredOnly"),
    BASIC_STEPS_MR_OTS_SQL_SERVER(4, "rCucumber-BasicSteps-MROTS-SQLServer-AutoTriggeredOnly"),
    BASIC_STEPS_NON_ASV_SQL_SERVER(5, "rCucumber-BasicSteps-NonASV-SQLServer-AutoTriggeredOnly"),
    FULL_QA_REGRESSION_SQL_SERVER(6, "rCucumber-FullQARegression-SQLServer-AutoTriggeredOnly")
}