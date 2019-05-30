package service

import model.Job
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.util.*

class CucumberReportServiceTest {
}

fun main(args: Array<String>) {
    //CucumberReportService().investigateStatus(Job.MANUAL_ORACLE_JOB)

    val dateformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val now = ZonedDateTime.of(ZonedDateTime.parse("2019-05-26T05:25:34Z").toLocalDateTime(), UTC)


    println(now.toOffsetDateTime())
    println(now.toLocalDateTime())
    println(now)
}