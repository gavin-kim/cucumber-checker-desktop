package service

import model.svn.SvnConnectionTest
import model.svn.SvnHistory
import model.svn.SvnLog
import org.tmatesoft.svn.core.SVNAuthenticationException
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNWCUtil
import tornadofx.Controller

private const val SVN_REVISION_HEAD = -1L
private const val SVN_REVISION_BASE = 0L
private const val SVN_SERVER = "https://wfmsvn.infor.com/svn"

class SvnService : Controller() {

    init {
        DAVRepositoryFactory.setup()
    }

    fun getRepositoryHistory(url: String): SvnHistory {
        val repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url))
        repository.authenticationManager = SVNWCUtil.createDefaultAuthenticationManager()

        val logs = arrayListOf<SvnLog>()
        repository.log(arrayOf(""), SVN_REVISION_BASE, SVN_REVISION_HEAD, true, true) {
            logs.add(SvnLog(it.revision, it.author, it.date, it.message))
        }

        return SvnHistory(
            url = url,
            baseRevision = logs.first().revision,
            headRevision = logs.last().revision,
            logs = logs
        )
    }

    fun testConnection(url: String): SvnConnectionTest {
        val repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url))
        repository.authenticationManager = SVNWCUtil.createDefaultAuthenticationManager()

        return testRepositoryConnection(repository)
    }

    fun testConnection(url: String, username: String, password: String): SvnConnectionTest {
        val repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url))
        val configDir = SVNWCUtil.getDefaultConfigurationDirectory()
        repository.authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(configDir, username, password.toCharArray(), true)

        return testRepositoryConnection(repository)
    }

    private fun testRepositoryConnection(repository: SVNRepository): SvnConnectionTest {
        return runCatching {
            repository.testConnection()
        }.run {
            when {
                isSuccess -> SvnConnectionTest.AUTHENTICATION_PASSED
                exceptionOrNull() is SVNAuthenticationException -> SvnConnectionTest.AUTHENTICATION_FAILED
                else -> SvnConnectionTest.CONNECTION_FAILED
            }
        }
    }
}