package service

import model.svn.SvnHistory
import model.svn.SvnLog
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNWCUtil
import tornadofx.Controller

private const val SVN_REVISION_HEAD = -1L
private const val SVN_REVISION_BASE = 0L

class SvnService : Controller() {

    fun getRepositoryHistory(url: String): SvnHistory {
        DAVRepositoryFactory.setup()

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
}