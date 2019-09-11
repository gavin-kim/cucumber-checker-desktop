package fragment.svnHistory

import event.DispatchSvnCredential
import fragment.MessagePopupFragment
import fragment.svnAuthentication.SvnAuthenticationFragment
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import model.cucumber.Build
import model.svn.SvnConnectionTest
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
        subscribe<DispatchSvnCredential> {

            val url = build.parameters["BranchToBuild"]!!

            when (svnService.testConnection(url, it.username, it.password)) {
                SvnConnectionTest.AUTHENTICATION_PASSED -> loadRepositoryHistory(url)
                SvnConnectionTest.AUTHENTICATION_FAILED -> showSvnAuthenticationPopup()
                SvnConnectionTest.CONNECTION_FAILED -> showSvnConnectionErrorPopup()
            }
        }

        buildProperty.addListener { _, oldValue, newValue ->
            if (newValue != null && oldValue != newValue) {
                buildRevision = build.revision

                val url = build.parameters["BranchToBuild"]!!

                when (svnService.testConnection(url)) {
                    SvnConnectionTest.AUTHENTICATION_PASSED -> loadRepositoryHistory(url)
                    SvnConnectionTest.AUTHENTICATION_FAILED -> showSvnAuthenticationPopup()
                    SvnConnectionTest.CONNECTION_FAILED -> showSvnConnectionErrorPopup()
                }
            }
        }
    }

    private fun loadRepositoryHistory(url: String) {
        runAsync {
            svnService.getRepositoryHistory(url)
        } success {
            svnHistory = it
            svnLogList.setAll(it.logs)
        }
    }

    private fun showSvnAuthenticationPopup() {
        find<SvnAuthenticationFragment>().openModal(block = true)
    }

    private fun showSvnConnectionErrorPopup() {
        find<MessagePopupFragment>(
            "message" to "Failed to connect to SVN server"
        ).openModal(block = true)
    }
}