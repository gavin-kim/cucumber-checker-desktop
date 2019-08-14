package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

private const val TEST_JOB = "ExecuteCucumberRun-Oracle-Parallel"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CucumberReportServiceTest {

    private val service = CucumberReportService()
    private val mapper = ObjectMapper().registerKotlinModule().writerWithDefaultPrettyPrinter()

    @Test
    fun `getBuilds`() {
        val builds = service.getBuilds(TEST_JOB)
        prettyPrint(builds)
    }

    @Test
    fun `getBuilds - NoReport`() {
        val builds = service.getBuilds(TEST_JOB)
        val noReportBuilds = builds.filter { it.finished && !it.hasReport }

        prettyPrint(noReportBuilds)
    }

    @Test
    fun `getReport - check all report has a screenshot`() {
        val a = "I \"saasdfg\" asdgfasddfsd \"sfettweawet\" \"saasdfg\" \"safwefwe\" asfwe."
        println(a)
        val normalizedA = a.replace("[\"][^\"]*[\"]".toRegex(), "\"?\"")
        println(normalizedA)

    }


    private fun prettyPrint(any: Any) {
        println(mapper.writeValueAsString(any))
    }
}
