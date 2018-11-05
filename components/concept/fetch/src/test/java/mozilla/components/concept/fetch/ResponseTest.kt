/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.fetch

import mozilla.components.support.test.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class ResponseTest {
    @Test
    fun `Creating String from Body`() {
        val stream = "Hello World".byteInputStream()

        val body = spy(Response.Body(stream))
        assertEquals("Hello World", body.string())

        verify(body).close()
    }

    @Test
    fun `Creating BufferedReader from Body`() {
        val stream = "Hello World".byteInputStream()

        val body = spy(Response.Body(stream))

        var readerUsed = false
        body.useBufferedReader { reader ->
            assertEquals("Hello World", reader.readText())
            readerUsed = true
        }

        assertTrue(readerUsed)

        verify(body).close()
    }

    @Test
    fun `Using InputStream from Body`() {
        val stream = "Hello World".byteInputStream()

        val body = spy(Response.Body(stream))

        var streamUsed = false
        body.useStream { stream ->
            assertEquals("Hello World", stream.bufferedReader().readText())
            streamUsed = true
        }

        assertTrue(streamUsed)

        verify(body).close()
    }

    @Test
    fun `Closing Body closes stream`() {
        val stream = spy("Hello World".byteInputStream())

        val body = spy(Response.Body(stream))
        body.close()

        verify(stream).close()
    }

    @Test
    fun `success() extension function returns true for 2xx response codes`() {
        assertTrue(Response("https://www.mozilla.org", 200, headers = mock(), body = mock()).success)
        assertTrue(Response("https://www.mozilla.org", 203, headers = mock(), body = mock()).success)

        assertFalse(Response("https://www.mozilla.org", 404, headers = mock(), body = mock()).success)
        assertFalse(Response("https://www.mozilla.org", 500, headers = mock(), body = mock()).success)
        assertFalse(Response("https://www.mozilla.org", 302, headers = mock(), body = mock()).success)
    }

    @Test
    fun `clientError() extension function returns true for 4xx response codes`() {
        assertTrue(Response("https://www.mozilla.org", 404, headers = mock(), body = mock()).clientError)
        assertTrue(Response("https://www.mozilla.org", 403, headers = mock(), body = mock()).clientError)

        assertFalse(Response("https://www.mozilla.org", 200, headers = mock(), body = mock()).clientError)
        assertFalse(Response("https://www.mozilla.org", 203, headers = mock(), body = mock()).clientError)
        assertFalse(Response("https://www.mozilla.org", 500, headers = mock(), body = mock()).clientError)
        assertFalse(Response("https://www.mozilla.org", 302, headers = mock(), body = mock()).clientError)
    }
}
