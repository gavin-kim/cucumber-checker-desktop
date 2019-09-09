package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.QueueItem
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI

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

    @Test
    fun `test jenkins api`() {
        val server = JenkinsServer(URI("http://wfm-ci.infor.com:8080"))

        val cases = server.getJob(TEST_JOB).getBuildByNumber(17626).testResult
            .suites
            .flatMap { it.cases }
            .filter { it.status != "PASSED" }
            .first()

        println(cases.errorDetails)
        println(cases.errorStackTrace)
    }

    @Test
    fun `getBriefReport`() {
        val builds = service.getBuilds("rCucumber-BasicSteps-MROTS-Oracle-AutoTriggeredOnly")

        val briefReports = builds
            .filter { it.hasReport }
            .map { service.getBriefReport(it) }

        prettyPrint(briefReports)
    }

    @Test
    fun `buildtest`() {
        val server = JenkinsServer(URI("http://wfm-ci.infor.com:8080"))

        val buildDetails = server.getJob(TEST_JOB).builds.map { it.details() }
        prettyPrint(buildDetails)
    }

    private fun prettyPrint(any: Any) {
        println(mapper.writeValueAsString(any))
    }
}
