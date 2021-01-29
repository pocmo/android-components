package mozilla.components.multiplatform.concept.fetch

import android.net.Uri
import okio.buffer
import okio.source
import okio.use
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

fun Request.Body.Companion.fromStream(stream: InputStream): Request.Body {
    return Request.Body(stream.source())
}

/**
 * Create a [Body] from the provided [String].
 */
fun Request.Body.Companion.fromString(value: String): Request.Body {
    return fromStream(value.byteInputStream())
}

/**
 * Create a [Body] from the provided [File].
 */
fun Request.Body.Companion.fromFile(file: File): Request.Body {
    return Request.Body(file.source())
}

/**
 * Create a [Body] from the provided [unencodedParams] in the format of Content-Type
 * "application/x-www-form-urlencoded". Parameters are formatted as "key1=value1&key2=value2..."
 * and values are percent-encoded. If the given map is empty, the response body will contain the
 * empty string.
 *
 * @see [Headers.Values.CONTENT_TYPE_FORM_URLENCODED]
 */
fun Request.Body.Companion.fromParamsForFormUrlEncoded(vararg unencodedParams: Pair<String, String>): Request.Body {
    // It's unintuitive to use the Uri class format and encode
    // but its GET query syntax is exactly what we need.
    val uriBuilder = Uri.Builder()
    unencodedParams.forEach { (key, value) -> uriBuilder.appendQueryParameter(key, value) }
    val encodedBody = uriBuilder.build().encodedQuery ?: "" // null when the given map is empty.
    return fromString(encodedBody)
}

fun Response.Body.Companion.fromStream(stream: InputStream): Response.Body {
    return Response.Body(stream.source())
}

/**
 * Creates an empty response body.
 */
fun Response.Body.Companion.empty() = Response.Body.fromStream("".byteInputStream())

@Suppress("TooGenericExceptionCaught")
val Response.Body.charset
    get() = contentType?.let {
        val charset = it.substringAfter("charset=")
        try {
            Charset.forName(charset)
        } catch (e: Exception) {
            Charsets.UTF_8
        }
    } ?: Charsets.UTF_8


/**
 * Creates a buffered reader from this body.
 *
 * Executes the given [block] function with the buffered reader as parameter and then closes it down correctly
 * whether an exception is thrown or not.
 *
 * @param charset the optional charset to use when decoding the body. If not specified,
 * the charset provided in the response content-type header will be used. If the header
 * is missing or the charset is not supported, UTF-8 will be used.
 * @param block a function to consume the buffered reader.
 *
 */
fun <R> Response.Body.useBufferedReader(charset: Charset? = null, block: (BufferedReader) -> R): R = useStream { source ->
    val reader = source.buffer().inputStream().bufferedReader(charset ?: this.charset)
    block(reader)
}

/**
 * Reads this body completely as a String.
 *
 * Takes care of closing the body down correctly whether an exception is thrown or not.
 *
 * @param charset the optional charset to use when decoding the body. If not specified,
 * the charset provided in the response content-type header will be used. If the header
 * is missing or the charset not supported, UTF-8 will be used.
 */
fun Response.Body.string(charset: Charset? = null): String = useBufferedReader(charset) { it.readText() }
