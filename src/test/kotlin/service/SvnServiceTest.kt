package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

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


    private fun prettyPrint(any: Any) {
        println(mapper.writeValueAsString(any))
    }
}