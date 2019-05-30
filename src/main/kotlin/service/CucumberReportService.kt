package service
import model.*
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

private const val CUCUMBER_HOST = "http://wfm-ci.infor.com:8080"
private const val CUCUMBER_HTML_REPORTS = "cucumber-html-reports"
private const val OVERVIEW_FEATURES = "overview-features.html"

class CucumberReportService {

    val logger = KotlinLogging.logger {}

    fun getBuilds(job: Job): List<Build> {
        val rssUrl = "$CUCUMBER_HOST/job/${job.jobName}/rssAll"

        val doc = Jsoup.connect(rssUrl).maxBodySize(0).get()

        return doc.select("feed > entry").map {
            val title = it.select("title").text()

            val id = it.select("id").text().substringAfterLast(":").toInt()
            val name = title.substringAfter(" ").substringBeforeLast("(").trim()

            val link = it.select("link").attr("href")

            val statusMessage = title.substringAfterLast("(").substringBefore(")")
            val status = getBuildStatus(statusMessage)

            Build(id, name, link, status)
        }
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
                failedFeatures.add(getFailedFeature(reportUrl, featureName, failedScenarioNames))
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

        val failedScenarioNameSet = failedScenarioNames.toSet()
        val failedScenarios = mutableListOf<Scenario>()

        val elements = doc.body().select("div.feature > div.elements > div.element")

        val hasBackground = elements
            .select("span.collapsable-control > div.brief > span.keyword:contains('Background')").isNotEmpty()

        elements.filter { element ->
            element.select("span.collapsable-control > div.brief > span.keyword").text().startsWith("Scenario")
        }.forEach { element ->
            val scenarioName = element.selectFirst("span.name").text()

            if (failedScenarioNameSet.contains(scenarioName)) {
                val scenarioTags = element.select("div.tags > a").eachText()

                var failedStepElement = element.select("div.step > div.brief.failed, div.hook > div.brief.failed")
                if (failedStepElement == null && hasBackground) {
                    failedStepElement = element.nextElementSibling().select("div.step > div.brief.failed")
                }
                //TODO: Scenario fail or Step failed or Hook failed( can be many)
                val failedStep = failedStepElement.select("span.name").text()
                val failedReason = failedStepElement.next("pre").text()

                val scenario = Scenario(scenarioTags.toSet(), scenarioName, failedStep, failedReason)

                failedScenarios.add(scenario)
            }
        }

        val featureTags = doc.body().select("div.feature > div.tags > a").eachText()

        return Feature(featureName, featureTags.toSet(), failedScenarios)
    }
}

