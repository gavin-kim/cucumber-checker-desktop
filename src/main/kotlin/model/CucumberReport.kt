package model

data class CucumberReport(
    val job: Job,
    val build: Build,
    val failedFeatures: List<Feature>
)

data class Feature(
    val name: String,
    val tags: Set<String>,
    val failedScenarios: List<Scenario>
)

data class Scenario(
    val tags: Set<String>,
    val name: String,
    val failedStep: String,
    val failedReason: String
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