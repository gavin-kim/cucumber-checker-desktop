package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNWCUtil

private const val TEST_BRANCH = "https://wfmsvn.infor.com/svn/wfm/WORKBRAIN/Source/projects/BranchesToBeIndexed-July2019/Gavin/WFM-21980_640"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SvnServiceTest {

    private val service = SvnService()
    private val mapper = ObjectMapper().registerKotlinModule().writerWithDefaultPrettyPrinter()

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun getBranchHistory() {
        val history = service.getRepositoryHistory(TEST_BRANCH)
        prettyPrint(history)
    }


    @Test
    fun `authentication test`() {
        DAVRepositoryFactory.setup()

        val repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded("https://wfmsvn.infor.com"))
        repository.authenticationManager = SVNWCUtil.createDefaultAuthenticationManager()
        SVNWCUtil.createDefaultAuthenticationManager()

        println()
    }

    private fun prettyPrint(any: Any) {
        println(mapper.writeValueAsString(any))
    }
}