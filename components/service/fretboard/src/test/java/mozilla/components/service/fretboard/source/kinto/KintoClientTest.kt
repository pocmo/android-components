/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.fretboard.source.kinto

import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.Request
import mozilla.components.concept.fetch.Response
import mozilla.components.support.test.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.net.URL

@RunWith(RobolectricTestRunner::class)
class KintoClientTest {
    private val baseUrl = "http://example.test"
    private val bucketName = "fretboard"
    private val collectionName = "experiments"

    @Test
    fun get() {
        val httpClient = MockedClient()
        val kintoClient = KintoClient(httpClient, baseUrl, bucketName, collectionName)
        kintoClient.get()

        val request = httpClient.lastRequest!!
        assertEquals(
            "http://example.test/buckets/fretboard/collections/experiments/records",
            request.url)
    }

    @Test
    fun diff() {
        val httpClient = MockedClient()
        val kintoClient = KintoClient(httpClient, baseUrl, bucketName, collectionName)
        kintoClient.diff(1527179995)

        val request = httpClient.lastRequest!!
        assertEquals(
            "http://example.test/buckets/fretboard/collections/experiments/records?_since=1527179995",
            request.url
        )
    }

    private class MockedClient(private val status: Int = 200): Client {
        var lastRequest: Request? = null

        override fun fetch(request: Request): Response {
            lastRequest = request
            return Response(status, Response.Body("Hello".byteInputStream()))
        }
    }
}