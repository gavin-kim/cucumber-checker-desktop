package service

import model.*
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import tornadofx.Controller

private const val CUCUMBER_HOST = "http://wfm-ci.infor.com:8080"
private const val CUCUMBER_HTML_REPORTS = "cucumber-html-reports"
private const val OVERVIEW_FEATURES = "overview-features.html"
private const val SCENARIO = "Scenario"
private const val BACKGROUND = "Background"

class CucumberReportService: Controller() {

    val logger = KotlinLogging.logger {}

    fun getBuilds(job: Job): List<Build> {
        val rssUrl = "$CUCUMBER_HOST/job/${job.jobName}/rssAll"

        val doc = Jsoup.connect(rssUrl).maxBodySize(0).get()

        return doc.select("feed > entry").map {
            val title = it.select("title").text()

            val id = it.select("id").text().substringAfterLast(":").toInt()
            val (name, statusMessage) = getNameStatusMessagePair(title.replace(job.jobName, ""))
            val link = it.select("link").attr("href")

            Build(id, name, link, getBuildStatus(statusMessage))
        }
    }

    private fun getNameStatusMessagePair(title: String): Pair<String, String> {

        var countLeft = 0
        var countRight = 0

        for (i in title.length - 1 downTo 0) {
            when (title[i]) {
                '(' -> countLeft++
                ')' -> countRight++
            }

            if (countRight > 0 && countLeft == countRight) {
                return title.substring(0, i).trim() to title.substring(i)
            }
        }
        return title.trim() to ""
    }

    private fun getBuildStatus(message: String): Build.Status {
        return when {
            message.contains("broken") -> Build.Status.BROKEN
            message.contains("back to normal") or message.contains("stable") -> Build.Status.STABLE
            message.contains("aborted") -> Build.Status.ABORTED
            else -> Build.Status.UNSTABLE
        }
    }

    fun investigateStatus(job: Job) {
        val rssUrl = "$CUCUMBER_HOST/job/${job.jobName}/rssAll"

        val doc = Jsoup.connect(rssUrl).maxBodySize(0).get()

        val messageToStatus = doc.select("feed > entry").map {
            val title = it.select("title").text()// status, title
            val statusMessage = title.substringAfterLast("(").substringBefore(")")

            val link = it.select("link").attr("href")
            val buildDoc = Jsoup.connect(link).maxBodySize(0).get()
            val status = buildDoc.body().select("h1.build-caption > img").attr("tooltip")

            statusMessage to status
        }

        messageToStatus.forEach {
            println(it)
        }
    }

    fun getReport(job: Job, build: Build): CucumberReport {
        val buildUrl = "$CUCUMBER_HOST/job/${job.jobName}/${build.id}"

        val failedScenarioNamesByFeatureName = getFailedScenarioNamesByFeatureName(buildUrl)
        val reportHtmlByFeature = getReportHtmlByFeature(buildUrl)

        val failedFeatures = mutableListOf<Feature>()

        failedScenarioNamesByFeatureName.forEach { (featureName, failedScenarioNames) ->
            if (reportHtmlByFeature.containsKey(featureName)) {
                val reportUrl = "$buildUrl/$CUCUMBER_HTML_REPORTS/${reportHtmlByFeature[featureName]}"
                val failedFeature = getFailedFeature(reportUrl, featureName, failedScenarioNames)
                failedFeatures.add(failedFeature)
            }
        }

        return CucumberReport(job, build, failedFeatures)
    }

    private fun getFailedScenarioNamesByFeatureName(buildUrl: String): Map<String, List<String>> {
        val doc = Jsoup.connect(buildUrl).maxBodySize(0).get()

        val failedTestNames = doc.body().select("a[href='testReport/']").next().select("li > a")
            .map { it.text().trim() }

        val actualFailedTestNames = failedTestNames
            .groupingBy { it }
            .eachCount()
            .filter { it.value >= 2 }
            .keys

        return actualFailedTestNames.groupBy({ it.substringBefore(".") }, { it.substringAfter(".") })
    }

    private fun getReportHtmlByFeature(buildUrl: String): Map<String, String> {
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
            }.toMap()
    }

    private fun getFailedFeature(reportUrl: String, featureName: String, failedScenarioNames: Collection<String>): Feature {

        val doc = Jsoup.connect(reportUrl).maxBodySize(0).get()

        val elements = doc.body().select("div.feature > div.elements > div.element")
        val elementsByKeyword = groupElementsByKeyword(elements)

        val featureTags = doc.body().select("div.feature > div.tags > a").eachText()

        val backgroundSteps =
            if (elementsByKeyword.contains(BACKGROUND)) getSteps(elementsByKeyword.getValue(BACKGROUND).first())
            else emptyList()

        val failedScenarios =
            if (elementsByKeyword.contains(SCENARIO)) getFailedScenarios(elementsByKeyword.getValue(SCENARIO), failedScenarioNames)
            else emptyList()

        return Feature(featureName, featureTags.toSet(), failedScenarios, backgroundSteps)
    }

    private fun groupElementsByKeyword(elements: List<Element>): Map<String, List<Element>> {
        return elements.groupBy { element ->
            element.select("span.collapsable-control > div.brief > span.keyword").text().trim()
        }
    }

    private fun getFailedScenarios(elements: List<Element>, failedScenarioNames: Collection<String>): List<Scenario> {
        val failedScenarioNameSet = failedScenarioNames.toSet()
        val failedScenarios = mutableListOf<Scenario>()

        elements.forEach { element ->
            val scenarioName = element.selectFirst("span.name").text()

            if (failedScenarioNameSet.contains(scenarioName)) {
                val scenarioTags = element.select("div.tags > a").eachText()

                val hooks = getHooks(element)
                val steps = getSteps(element)
                val screenShotLinks = getScreenShotLinks(element)

                val scenario = Scenario(scenarioTags.toSet(), scenarioName, hooks, steps, screenShotLinks)

                failedScenarios.add(scenario)
            }
        }

        return failedScenarios
    }

    private fun getScreenShotLinks(element: Element): List<String> {
        val lastStep = element.select("div.step").last()
        return lastStep.select("div.embeddings div.embedding-content > img").eachAttr("src")
    }

    private fun getHooks(element: Element): List<Hook> {
        return element.select("div.hook").map { hook ->
            val brief = hook.selectFirst("div.brief")

            val type = when (brief.select("keyword").text().trim()) {
                Hook.Type.BEFORE.text -> Hook.Type.BEFORE
                Hook.Type.AFTER.text -> Hook.Type.AFTER
                else -> Hook.Type.UNKNOWN
            }

            val name = brief.select("span.name").text()
            val duration = brief.select("span.duration").text()
            val result = getResult(brief)

            Hook(type, name, duration, result)
        }
    }

    private fun getSteps(element: Element): List<Step> {
        return element.select("div.step").map { step ->
            val brief = step.selectFirst("div.brief")

            val type = when (brief.select("keyword").text().trim()) {
                Step.Type.GIVEN.text -> Step.Type.GIVEN
                Step.Type.WHEN.text -> Step.Type.WHEN
                Step.Type.AND.text -> Step.Type.AND
                Step.Type.THEN.text -> Step.Type.THEN
                else -> Step.Type.UNKNOWN
            }
            val name = brief.select("span.name").text()
            val duration = brief.select("span.duration").text()
            val result = getResult(brief)

            val arguments = getStepArguments(step)
            val messages = if (result == Result.FAILED) getStepMessages(step) else emptyList()

            Step(type, name, duration, result, messages, arguments)
        }
    }

    private fun getResult(brief: Element): Result {
        return when {
            brief.hasClass(Result.PASSED.cssClass) -> Result.PASSED
            brief.hasClass(Result.FAILED.cssClass) -> Result.FAILED
            brief.hasClass(Result.UNDEFINED.cssClass) -> Result.UNDEFINED
            brief.hasClass(Result.SKIPPED.cssClass) -> Result.SKIPPED
            else -> Result.UNKNOWN
        }
    }

    private fun getStepMessages(step: Element): List<String> {
        return step.select("div.inner-level > div.message > div[id^='msg'] > pre").eachText()
    }

    private fun getStepArguments(step: Element): List<List<String>> {
        return step.select("table.step-arguments > tbody > tr").map { tr -> tr.select("td").eachText() }
    }
}

