/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.fetch

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ClientTest {
    @Test
    fun `Verify default user agent format`() {
        val client = TestClient()

        val userAgent = client.exposeUserAgent()

        assertTrue(userAgent.matches("MozacFetch/[0-9]+\\.[0-9]+\\.[0-9]+".toRegex()))
    }

    @Test
    fun `Async request with coroutines`() = runBlocking {
        val client = TestClient()
        val request = Request("https://www.mozilla.org")

        val deferredResponse = async { client.fetch(request) }

        val body = deferredResponse.await().body.string()
    }
}

private class TestClient(
    private val responseUrl: String? = null,
    private val responseStatus: Int = 200,
    private val responseHeaders: Headers = MutableHeaders(),
    private val responseBody: Response.Body = Response.Body.empty()
) : Client() {
    override fun fetch(request: Request): Response {
        return Response(responseUrl ?: request.url, responseStatus, responseHeaders, responseBody)
    }

    fun exposeUserAgent() = createBaseUserAgent()
}
