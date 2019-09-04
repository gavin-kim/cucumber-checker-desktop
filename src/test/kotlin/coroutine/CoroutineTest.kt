package coroutine

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant


private const val TEST_JOB = "ExecuteCucumberRun-Oracle-Parallel"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CoroutineTest {

    @Test
    fun performanceTester() = runBlocking {

        val start = Instant.now()

        runBlocking {
            // do something
        }

        val end = Instant.now()
    }
}