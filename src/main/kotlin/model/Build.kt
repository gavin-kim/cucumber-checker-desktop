package model

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