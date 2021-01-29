/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.multiplatform.concept.fetch

import mozilla.components.multiplatform.concept.fetch.Request.CookiePolicy
import okio.Closeable
import okio.IOException
import okio.Source
import okio.use

/**
 * The [Request] data class represents a resource request to be send by a [Client].
 *
 * It's API is inspired by the Request interface of the Web Fetch API:
 * https://developer.mozilla.org/en-US/docs/Web/API/Request
 *
 * @property url The URL of the request.
 * @property method The request method (GET, POST, ..)
 * @property headers Optional HTTP headers to be send with the request.
 * @property connectTimeout A timeout to be used when connecting to the resource.  If the timeout expires before the
 * connection can be established, a [java.net.SocketTimeoutException] is raised. A timeout of zero is interpreted as an
 * infinite timeout.
 * @property readTimeout A timeout to be used when reading from the resource. If the timeout expires before there is
 * data available for read, a java.net.SocketTimeoutException is raised. A timeout of zero is interpreted as an infinite
 * timeout.
 * @property body An optional body to be send with the request.
 * @property redirect Whether the [Client] should follow redirects (HTTP 3xx) for this request or not.
 * @property cookiePolicy A policy to specify whether or not cookies should be
 * sent with the request, defaults to [CookiePolicy.INCLUDE]
 * @property useCaches Whether caches should be used or a network request
 * should be forced, defaults to true (use caches).
 *
 * @see [Headers.Names]
 * @see [Headers.Values]
 */
data class Request(
    val url: String,
    val method: Method = Method.GET,
    val headers: MutableHeaders? = MutableHeaders(),
    // TODO
    // val connectTimeout: Pair<Long, TimeUnit>? = null,
    // val readTimeout: Pair<Long, TimeUnit>? = null,
    val body: Body? = null,
    val redirect: Redirect = Redirect.FOLLOW,
    val cookiePolicy: CookiePolicy = CookiePolicy.INCLUDE,
    val useCaches: Boolean = true
) {
    /**
     * A [Body] to be send with the [Request].
     *
     * @param stream A stream that will be read and send to the resource.
     */
    class Body(
        private val source: Source,
    ) : Closeable {
        /**
         * Executes the given [block] function on the body's stream and then closes it down correctly whether an
         * exception is thrown or not.
         */
        fun <R> useStream(block: (Source) -> R): R = use {
            block(source)
        }

        /**
         * Closes this body and releases any system resources associated with it.
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

    /**
     * Request methods.
     *
     * The request method token is the primary source of request semantics;
     * it indicates the purpose for which the client has made this request
     * and what is expected by the client as a successful result.
     *
     * https://tools.ietf.org/html/rfc7231#section-4
     */
    enum class Method {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE
    }

    enum class Redirect {
        /**
         * Automatically follow redirects.
         */
        FOLLOW,

        /**
         * Do not follow redirects and let caller handle them manually.
         */
        MANUAL
    }

    enum class CookiePolicy {
        /**
         * Include cookies when sending the request.
         */
        INCLUDE,

        /**
         * Do not send cookies with the request.
         */
        OMIT
    }
}

/**
 * Checks whether or not the request is for a data URI.
 */
fun Request.isDataUri() = url.startsWith("data:")

/**
 * Checks whether or not the request is for a data blob.
 */
fun Request.isBlobUri() = url.startsWith("blob:")
