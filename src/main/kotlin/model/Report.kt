package model

data class Report(
    val job: Job,
    val build: Build,
    val failedFeatures: List<Feature>
)