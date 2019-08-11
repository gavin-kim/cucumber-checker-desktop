package model

data class Report(
    val build: Build,
    val type: Type,
    val path: String,
    val failedFeatures: List<Feature>
) {
    enum class Type {
        FILE,
        WEB
    }
}
