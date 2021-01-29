/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.multiplatform.concept.fetch

import mozilla.components.multiplatform.concept.fetch.Response.Body
import mozilla.components.multiplatform.concept.fetch.Response.Companion.CLIENT_ERROR_STATUS_RANGE
import mozilla.components.multiplatform.concept.fetch.Response.Companion.SUCCESS_STATUS_RANGE
import okio.Closeable
import okio.IOException
import okio.Source
import okio.use

/**
 * The [Response] data class represents a response to a [Request] send by a [Client].
 *
 * You can create a [Response] object using the constructor, but you are more likely to encounter a [Response] object
 * being returned as the result of calling [Client.fetch].
 *
 * A [Response] may hold references to other resources (e.g. streams). Therefore it's important to always close the
 * [Response] object or its [Body]. This can be done by either consuming the content of the [Body] with one of the
 * available methods or by using Kotlin's extension methods for using [Closeable] implementations (like `use()`):
 *
 * ```Kotlin
 * val response = ...
 * response.use {
 *    // Use response. Resources will get released automatically at the end of the block.
 * }
 * ```
 */
data class Response(
    val url: String,
    val status: Int,
    val headers: Headers,
    val body: Body
) : Closeable {
    /**
     * Closes this [Response] and its [Body] and releases any system resources associated with it.
     */
    override fun close() {
        body.close()
    }

    /**
     * A [Body] returned along with the [Request].
     *
     * **The response body can be consumed only once.**.
     *
     * @param stream the input stream from which the response body can be read.
     * @param contentType optional content-type as provided in the response
     * header. If specified, an attempt will be made to look up the charset
     * which will be used for decoding the body. If not specified, or if the
     * charset can't be found, UTF-8 will be used for decoding.
     */
    open class Body(
        private val source: Source,
        val contentType: String? = null
    ) : Closeable {

        /**
         * Creates a usable stream from this body.
         *
         * Executes the given [block] function with the stream as parameter and then closes it down correctly
         * whether an exception is thrown or not.
         */
        fun <R> useStream(block: (Source) -> R): R = use {
            block(source)
        }

        /**
         * Closes this [Body] and releases any system resources associated with it.
         */
        override fun close() {
            try {
                source.close()
            } catch (e: IOException) {
                // Ignore
            }
        }

        companion object
    }

    companion object {
        val SUCCESS_STATUS_RANGE = 200..299
        val CLIENT_ERROR_STATUS_RANGE = 400..499
        const val SUCCESS = 200
        const val CONTENT_TYPE_HEADER = "Content-Type"
        const val CONTENT_LENGTH_HEADER = "Content-Length"
    }
}

/**
 * Returns true if the response was successful (status in the range 200-299) or false otherwise.
 */
val Response.isSuccess: Boolean
    get() = status in SUCCESS_STATUS_RANGE

/**
 * Returns true if the response was a client error (status in the range 400-499) or false otherwise.
 */
val Response.isClientError: Boolean
    get() = status in CLIENT_ERROR_STATUS_RANGE
