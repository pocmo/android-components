/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.multiplatform.concept.fetch

import android.util.Base64
import mozilla.components.multiplatform.concept.fetch.Response.Companion.CONTENT_LENGTH_HEADER
import mozilla.components.multiplatform.concept.fetch.Response.Companion.CONTENT_TYPE_HEADER
import okio.source
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset

private const val DATA_URI_BASE64_EXT = ";base64"
private const val DATA_URI_SCHEME = "data:"
private const val DATA_URI_CHARSET = "charset="

/**
 * Generates a [Response] based on the provided [Request] for a data URI.
 *
 * @param request The [Request] for the data URI.
 * @return The generated [Response] including the decoded bytes as body.
 */
@Suppress("ComplexMethod", "TooGenericExceptionCaught")
fun Client.fetchDataUri(request: Request): Response {
    if (!request.isDataUri()) {
        throw IOException("Not a data URI")
    }
    return try {
        val dataUri = request.url

        val (contentType, bytes) = if (dataUri.contains(DATA_URI_BASE64_EXT)) {
            dataUri.substringAfter(DATA_URI_SCHEME).substringBefore(DATA_URI_BASE64_EXT) to
                Base64.decode(dataUri.substring(dataUri.lastIndexOf(',') + 1), Base64.DEFAULT)
        } else {
            val contentType = dataUri.substringAfter(DATA_URI_SCHEME).substringBefore(",")
            val charset = if (contentType.contains(DATA_URI_CHARSET)) {
                Charset.forName(contentType.substringAfter(DATA_URI_CHARSET).substringBefore(","))
            } else {
                Charsets.UTF_8
            }
            contentType to
                URLDecoder.decode(dataUri.substring(dataUri.lastIndexOf(',') + 1), charset.name()).toByteArray()
        }

        val headers = MutableHeaders().apply {
            set(CONTENT_LENGTH_HEADER, bytes.size.toString())
            if (contentType.isNotEmpty()) {
                set(CONTENT_TYPE_HEADER, contentType)
            }
        }

        Response(
            dataUri,
            Response.SUCCESS,
            headers,
            Response.Body(ByteArrayInputStream(bytes).source(), contentType)
        )
    } catch (e: Exception) {
        throw IOException("Failed to decode data URI")
    }
}
