package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import model.Report
import mu.KotlinLogging
import tornadofx.Controller
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

class PersistenceService : Controller() {

    private val logger = KotlinLogging.logger {}
    private val mapper = ObjectMapper().registerKotlinModule()

    fun save(report: Report, directory: File) {
        val directoryToSave = File("${directory.absolutePath}/${report.build.job}-${report.build.id}")

        val reportFilePath = "$directoryToSave/report.json"

        if (directoryToSave.exists() || directoryToSave.mkdirs()) {
            mapper.writeValue(File(reportFilePath), report)
        }

        saveScreenShots(report, directoryToSave)
    }

    private fun saveScreenShots(report: Report, directory: File) {
        val directoryPath = directory.absolutePath

        val files = report.failedFeatures.flatMap { feature ->
            feature.failedScenarios.flatMap { scenario ->
                scenario.screenShotFiles
            }
        }

        files.forEach { file ->
            val url = URL("${report.path}/$file")
            val readableByteChannel = Channels.newChannel(url.openStream())
            val outputFile = File("$directoryPath/$file")
            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
            val fileOutputStream = FileOutputStream(outputFile)

            fileOutputStream.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
        }
    }

    fun load(directory: File): Report {
        val reportFileToLoad = File("${directory.absolutePath}/report.json")

        if (reportFileToLoad.exists() && reportFileToLoad.isFile) {
            val report = mapper.readValue(reportFileToLoad, Report::class.java)
            return updateReport(report, directory)
        } else {
            throw IllegalStateException("Cannot find report.json file in ${directory.absolutePath}")
        }
    }

    private fun updateReport(report: Report, directory: File): Report {
        return Report(
            report.build,
            Report.Type.FILE,
            directory.absolutePath,
            report.failedFeatures
        )
    }
}