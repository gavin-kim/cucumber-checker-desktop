package fragment.svnHistory

import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.cucumber.Build
import model.svn.SvnHistory
import model.svn.SvnLog
import service.SvnService
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.getValue
import tornadofx.listProperty
import tornadofx.observableListOf
import tornadofx.property
import tornadofx.success


class SvnHistoryController : Controller() {

    private val svnService: SvnService by inject()

    private var build: Build by property()
    val buildProperty = getProperty(SvnHistoryController::build)

    private var buildRevision: Int by property()
    val buildRevisionProperty = getProperty(SvnHistoryController::buildRevision)

    private var svnHistory: SvnHistory by property()

    private val svnLogList: ObservableList<SvnLog> by listProperty(observableListOf())
    val svnLogListProperty = SimpleListProperty(svnLogList)

    private var selectedSvnLog: SvnLog by property()
    val selectedSvnLogProperty = getProperty(SvnHistoryController::selectedSvnLog)

    init {
        buildProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                buildRevision = build.revision
                val url = build.parameters["BranchToBuild"]!!

                runAsync {
                    svnService.getRepositoryHistory(url)
                } success {
                    svnHistory = it
                    svnLogList.setAll(it.logs)
                }
            }
        }
    }
}