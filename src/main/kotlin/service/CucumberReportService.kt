package service

import model.cucumber.BriefReport
import model.cucumber.Build
import model.cucumber.Feature
import model.cucumber.Report
import model.cucumber.Scenario
import model.cucumber.Step
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import tornadofx.Controller
import java.net.URL

private const val DEFAULT_SERVER_URL = "http://wfm-ci.infor.com:8080"
private const val CUCUMBER_HTML_REPORTS = "cucumber-html-reports"
private const val OVERVIEW_FEATURES = "overview-features.html"
private const val TEST_REPORT = "testReport"
private const val SCENARIO = "Scenario"
private const val BACKGROUND = "Background"
private const val CONSOLE_TEXT = "consoleText"
private const val XML_BUILD_SEARCH_QUERY = "xml?tree=builds[building,id,displayName,result,duration,timestamp,actions[causes[*],parameters[*]]]"
private const val XML_TEST_REPORT_FAILED_RESULT_QUERY = "xml?tree=suites[cases[className,name,status]]&xpath=/testResult/suite/case/status[contains(text(),'REGRESSION') or contains(text(),'FAILED')]/..&wrapper=failedResult"
private const val XML_JOB_NAMES_QUERY = "xml?tree=jobs[name]"

class CucumberReportService : Controller() {

    private val logger = KotlinLogging.logger {}

    //private val serverUrl = app.config.string("server.url", DEFAULT_SERVER_URL)
    private val serverUrl = DEFAULT_SERVER_URL

    fun getBuilds(job: String): List<Build> {
        val jobUrl = "$serverUrl/job/$job"
        val url = "$jobUrl/api/$XML_BUILD_SEARCH_QUERY"

        val xml = Jsoup.connect(url).maxBodySize(0).get()

        return xml.select("build").map { build ->
            val id = build.select("id").text().toInt()
            val name = build.select("displayName").text()
            val result = getBuildResult(build.select("result").text())
            val duration = build.select("duration").text().toLong()
            val timestamp = build.select("timestamp").text().toLong()
            val finished = build.select("building").text().equals("true", true).not()
            val hasReport = build.select("action[_class='hudson.tasks.junit.TestResultAction']").isNotEmpty()
            val revision = getBuildRevision("$serverUrl/job/$job/$id")
            val parameters = getBuildParameters(build.select("action[_class='hudson.model.ParametersAction'] > parameter"))

            val (userId, userName) = build.select("action[_class='hudson.model.CauseAction'] > cause")
                .let { it.select("userId").text() to it.select("userName").text() }

            Build(
                job = job,
                id = id,
                name = name,
                result = result,
                duration = duration,
                timestamp = timestamp,
                finished = finished,
                hasReport = hasReport,
                userId = userId,
                userName = userName,
                revision = revision,
                parameters = parameters
            )
        }
    }

    private fun getBuildResult(result: String): Build.Result {
        return when (result) {
            "SUCCESS" -> Build.Result.SUCCESS
            "FAILURE" -> Build.Result.FAILURE
            "UNSTABLE" -> Build.Result.UNSTABLE
            "ABORTED" -> Build.Result.ABORTED
            else -> Build.Result.UNKNOWN
        }
    }

    private fun getBuildRevision(buildUrl: String): Int {
        val url = URL("$buildUrl/$CONSOLE_TEXT")

        return url.openStream().bufferedReader().use {
            var line: String? = it.readLine()

            while (line != null) {
                if (line.contains("At revision")) {
                    return@use line.replace("At revision ", "").toInt()
                } else {
                    line = it.readLine()
                }
            }
            return@use 0
        }
    }

    private fun getBuildParameters(parameters: Collection<Element>): Map<String, String> {
        return parameters.associate { parameter ->
            parameter.select("name").text() to parameter.select("value").text()
        }
    }

    fun getReport(build: Build): Report {
        val buildUrl = "$serverUrl/job/${build.job}/${build.id}"

        val scenarioFailedCountMapByFeatureName = getScenarioFailedCountMapByFeatureName(buildUrl)

        val htmlFilesByFeatureName = getHtmlFilesByFeatureName(buildUrl)
        val reportUrl = "$buildUrl/$CUCUMBER_HTML_REPORTS"

        val failedFeatures = scenarioFailedCountMapByFeatureName.flatMap { (featureName, scenarioFailedCountMap) ->
            htmlFilesByFeatureName.getOrElse(featureName) { emptyList() }.map { htmlFile ->
                getFailedFeature("$reportUrl/$htmlFile", featureName, scenarioFailedCountMap)
            }
        }

        return Report(build, Report.Type.WEB, reportUrl, failedFeatures)
    }

    private fun getScenarioFailedCountMapByFeatureName(buildUrl: String): Map<String, Map<String, Int>> {
        val url = "$buildUrl/$TEST_REPORT/api/$XML_TEST_REPORT_FAILED_RESULT_QUERY"

        val xml = Jsoup.connect(url).maxBodySize(0).get()

        val featureScenarioNamePairs = xml.select("case")
            .map { it.select("className").text() to it.select("name").text() }

        return featureScenarioNamePairs
            .groupingBy { it }
            .eachCount()
            .entries
            .groupBy({ it.key.first }, { it.key.second to it.value })
            .mapValues { (_, value) -> value.toMap() }
    }

    private fun getHtmlFilesByFeatureName(buildUrl: String): Map<String, List<String>> {
        val response = Jsoup.connect("$buildUrl/$CUCUMBER_HTML_REPORTS/$OVERVIEW_FEATURES")
            .maxBodySize(0)
            .ignoreHttpErrors(true)
            .execute()

        if (response.statusCode() != HttpStatus.SC_OK) return emptyMap()

        val doc = response.parse()
        return doc.body()
            .select("table[id='tablesorter'] > tbody > tr")
            .map { failedFeatureReport ->
                val tagColumn = failedFeatureReport.child(0)
                tagColumn.text() to tagColumn.select("a").attr("href")
            }.groupBy({ it.first }, { it.second })
    }

    private fun getFailedFeature(featureUrl: String, featureName: String, scenarioFailedCountMap: Map<String, Int>): Feature {

        val doc = Jsoup.connect(featureUrl).maxBodySize(0).get()

        val elements = doc.body().select("div.feature > div.elements > div.element")

        val featureTags = doc.body().select("div.feature > div.tags > a").eachText()
        val failedScenarios = getFailedScenarios(elements, scenarioFailedCountMap)

        return Feature(featureName, featureTags.distinct(), failedScenarios)
    }

    private fun getFailedScenarios(elements: List<Element>, scenarioFailedCountMap: Map<String, Int>): List<Scenario> {

        val failedScenarios = mutableListOf<Scenario>()

        val scenarioElements = elements.filter { getElementKeyword(it) == SCENARIO }

        scenarioElements.forEach { element ->
            val scenarioName = element.selectFirst("span.name").text()
            val isFailedScenario = element.select("span > div.brief").hasClass("failed")

            if (scenarioFailedCountMap.contains(scenarioName) && isFailedScenario) {
                val scenarioTags = element.select("div.tags > a").eachText()

                val hooks = getHooks(element)
                val backgroundSteps = getBackgroundSteps(element)
                val steps = getSteps(element)
                val screenShotLinks = getScreenShotLinks(element)
                val unstable = scenarioFailedCountMap.getValue(scenarioName) < 2

                val scenario = Scenario(
                    scenarioTags.distinct(),
                    scenarioName,
                    hooks,
                    backgroundSteps,
                    steps,
                    screenShotLinks,
                    unstable
                )

                failedScenarios.add(scenario)
            }
        }

        return failedScenarios
    }

    private fun getElementKeyword(element: Element): String {
        return element.select("span.collapsable-control > div.brief > span.keyword").text().trim()
    }

    private fun getScreenShotLinks(element: Element): List<String> {
        val lastStep = element.select("div.step").last()
        return lastStep.select("div.embeddings div.embedding-content > img").eachAttr("src")
    }

    private fun getHooks(element: Element): List<Step> {
        return element.select("div.hook").map { hook -> getHook(hook) }
    }

    private fun getHook(hook: Element): Step {
        val brief = hook.selectFirst("div.brief")

        val keyword = when (brief.select("span.keyword").text().trim()) {
            Step.Keyword.BEFORE.text -> Step.Keyword.BEFORE
            Step.Keyword.AFTER.text -> Step.Keyword.AFTER
            else -> Step.Keyword.UNKNOWN
        }

        val name = brief.select("span.name").text()
        val duration = brief.select("span.duration").text()
        val result = getResult(brief)

        return Step(Step.Type.HOOK, keyword, name, duration, result)
    }

    private fun getBackgroundSteps(element: Element): List<Step> {
        val previousElement = element.previousElementSibling()

        return if (previousElement == null || getElementKeyword(previousElement) != BACKGROUND) {
            emptyList()
        } else {
            getSteps(previousElement)
        }
    }

    private fun getSteps(element: Element): List<Step> {
        return element.select("div.step").map { step -> getStep(step) }
    }

    private fun getStep(step: Element): Step {
        val brief = step.selectFirst("div.brief")

        val keyword = when (brief.select("span.keyword").text().trim()) {
            Step.Keyword.GIVEN.text -> Step.Keyword.GIVEN
            Step.Keyword.WHEN.text -> Step.Keyword.WHEN
            Step.Keyword.AND.text -> Step.Keyword.AND
            Step.Keyword.THEN.text -> Step.Keyword.THEN
            else -> Step.Keyword.UNKNOWN
        }
        val name = brief.select("span.name").text()
        val duration = brief.select("span.duration").text()
        val result = getResult(brief)

        val arguments = getStepArguments(step)
        val messages = if (result == Step.Result.FAILED) getStepMessages(step) else emptyList()

        return Step(Step.Type.STEP, keyword, name, duration, result, messages, arguments)
    }

    private fun getResult(brief: Element): Step.Result {
        return when {
            brief.hasClass(Step.Result.PASSED.cssClass) -> Step.Result.PASSED
            brief.hasClass(Step.Result.FAILED.cssClass) -> Step.Result.FAILED
            brief.hasClass(Step.Result.UNDEFINED.cssClass) -> Step.Result.UNDEFINED
            brief.hasClass(Step.Result.SKIPPED.cssClass) -> Step.Result.SKIPPED
            else -> Step.Result.UNKNOWN
        }
    }

    private fun getStepMessages(step: Element): List<String> {
        return step.select("div.inner-level > div.message > div[id^='msg'] > pre").eachText()
    }

    private fun getStepArguments(step: Element): List<List<String>> {
        return step.select("table.step-arguments > tbody > tr").map { tr -> tr.select("td").eachText() }
    }

    fun getCucumberJobs(): List<String> {
        val url = "$serverUrl/api/$XML_JOB_NAMES_QUERY"

        val xml = Jsoup.connect(url).maxBodySize(0).get()

        return xml.select("job")
            .map { it.select("name").text() }
            .filter { it.contains("cucumber", true) }
    }

    fun getBriefReport(build: Build): BriefReport {
        val buildUrl = "$serverUrl/job/${build.job}/${build.id}"

        val failedScenariosByFeature = getScenarioFailedCountMapByFeatureName(buildUrl)
            .mapValues { it.value.filter { (_, count) -> count > 1 }.keys }

        val reportUrl = "$buildUrl/$CUCUMBER_HTML_REPORTS"

        val featureScenarioNames = failedScenariosByFeature
            .flatMap { (feature, failedScenarios) -> failedScenarios.map { feature to it } }

        return BriefReport(build, reportUrl, featureScenarioNames)
    }
}

