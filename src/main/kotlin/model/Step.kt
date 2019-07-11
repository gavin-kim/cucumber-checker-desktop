package model

data class Step(
    val type: Type,
    val keyword: Keyword,
    val name: String,
    val duration: String,
    val result: Result,
    val messages: List<String> = emptyList(),
    val arguments: List<List<String>> = emptyList()
) {
    enum class Type {
        HOOK,
        BACKGROUND_STEP,
        STEP
    }

    enum class Keyword(val text: String) {
        BEFORE("Before"),
        AFTER("After"),
        GIVEN("Given"),
        AND("And"),
        WHEN("When"),
        THEN("Then"),
        UNKNOWN("")
    }
}