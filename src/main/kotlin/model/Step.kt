package model

data class Step(
    val keyword: Keyword,
    val name: String,
    val duration: String,
    val result: Result,
    val messages: List<String>,
    val arguments: List<List<String>>
) {
    enum class Keyword(val text: String) {
        GIVEN("Given"),
        AND("And"),
        WHEN("When"),
        THEN("Then"),
        UNKNOWN("")
    }
}