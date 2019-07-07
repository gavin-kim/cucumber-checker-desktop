package model

data class Report(
    val jobName: String,
    val buildId: Int,
    val buildUrl: String,
    val failedFeatures: List<Feature>
)