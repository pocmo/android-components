/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.lib.fetch.okhttp

import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.Request
import mozilla.components.concept.fetch.Response
import mozilla.components.concept.fetch.createBaseUserAgent
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

/**
 *
 */
class HttpUrlConnectionClient : Client {
    @Throws(IOException::class)
    override fun fetch(request: Request): Response {
        var connection: HttpURLConnection? = null

        connection = (URL(request.url).openConnection() as HttpURLConnection)

        connection.requestMethod = request.method.name

        connection.instanceFollowRedirects = request.redirect == Request.Redirect.FOLLOW

        request.connectTimeout?.let { connection.connectTimeout = it.second.toMillis(it.first).toInt() }
        request.readTimeout?.let { connection.readTimeout = it.second.toMillis(it.first).toInt() }

        // HttpUrlConnection sets an "Accept" header by default. We do not want that.
        connection.setRequestProperty("Accept", "*/*")

        connection.setRequestProperty("Accept-Encoding", "gzip")

        connection.setRequestProperty("User-Agent", "${createBaseUserAgent()} (HttpUrlConnection)")

        request.headers?.forEach { header ->
            connection.setRequestProperty(header.name, header.value)
        }

        request.body?.let { body ->
            connection.doOutput = true

            body.useStream { inStream ->
                val outStream = connection.outputStream

                inStream
                    .buffered()
                    .copyTo(connection.outputStream)


                outStream.flush()
                outStream.close()
            }
        }

        return Response(
            connection.responseCode,
            createBody(connection)
        )
    }
}

private fun createBody(connection: HttpURLConnection): Response.Body {
    val gzipped = connection.contentEncoding == "gzip"

    withFileNotFoundExceptionIgnored {
        return HttpUrlConnectionBody(connection, connection.inputStream, gzipped)
    }

    withFileNotFoundExceptionIgnored {
        return HttpUrlConnectionBody(connection, connection.errorStream, gzipped)
    }

    return EmptyBody()
}

private class EmptyBody: Response.Body("".byteInputStream())

private class HttpUrlConnectionBody(
    private val connection: HttpURLConnection,
    stream: InputStream,
    gzipped: Boolean
): Response.Body(if (gzipped) GZIPInputStream(stream) else stream) {
    override fun close() {
        super.close()

        connection.disconnect()
    }
}

private inline fun withFileNotFoundExceptionIgnored(block: () -> Unit) {
    try {
        block()
    } catch (e: FileNotFoundException) {
        // Ignore
    }
}
