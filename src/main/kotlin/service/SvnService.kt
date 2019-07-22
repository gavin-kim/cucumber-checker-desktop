package service

import tornadofx.*
import java.time.ZonedDateTime

private const val SVN_WORKBRAIN_SOURCE_URL = "https://wfmsvn.infor.com/svn/wfm/WORKBRAIN/Source"

class SvnService : Controller() {

    fun getRevision(branch: String, time: ZonedDateTime): Int {
        return TODO()
    }
}