package model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Build(
    val job: String,
    val id: Int,
    val name: String,
    val result: Result,
    val duration: Long,
    val timestamp: Long,
    val finished: Boolean,
    val hasReport: Boolean,
    val userId: String,
    val userName: String
) {
    @JsonIgnore
    val text =
        if (userId.isBlank()) "$id, $name"
        else "$id, $name ($userId, $userName)"

    enum class Result {
        SUCCESS,
        FAILURE,
        UNSTABLE,
        ABORTED,
        UNKNOWN
    }
}