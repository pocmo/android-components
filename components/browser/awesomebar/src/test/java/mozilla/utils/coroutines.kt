package mozilla.utils

import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Create single threaded dispatcher for test environment.
 */
fun createTestCoroutinesDispatcher(): CoroutineDispatcher =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()

/**
 * Call `@Before` test to setup test coroutines dispatcher as `Main`.
 */
@ExperimentalCoroutinesApi
fun setupTestCoroutinesDispatcher() {
    Dispatchers.setMain(createTestCoroutinesDispatcher())
}

/**
 * Call `@After` test to reset `Main` coroutines dispatcher.
 */
@ExperimentalCoroutinesApi
fun unsetTestCoroutinesDispatcher() {
    Dispatchers.resetMain()
}
