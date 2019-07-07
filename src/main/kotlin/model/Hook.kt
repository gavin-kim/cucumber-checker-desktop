package model

data class Hook(
    val keyword: Keyword,
    val name: String,
    val duration: String,
    val result: Result,
    val message: String? = null
) {
    enum class Keyword(val text: String) {
        BEFORE("Before"),
        AFTER("After"),
        UNKNOWN("")
    }
}