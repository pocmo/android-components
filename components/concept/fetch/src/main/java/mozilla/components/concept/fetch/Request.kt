/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.fetch

import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * The [Request] data class represents a resource request.
 *
 * It's API is inspired by the Request interface of the Web Fetch API:
 * https://developer.mozilla.org/en-US/docs/Web/API/Request
 *
 * @property url
 * @property method
 * @property headers
 * @property connectTimeout
 * @property readTimeout
 * @property body
 * @property redirect
 */
data class Request(
    val url: String,
    val method: Method = Method.GET,
    val headers: MutableHeaders? = MutableHeaders(),
    val connectTimeout: Pair<Long, TimeUnit>? = null,
    val readTimeout: Pair<Long, TimeUnit>? = null,
    val body: Body? = null,
    val redirect: Redirect = Redirect.FOLLOW
) {
    /**
     * TODO
     */
    class Body(
        private val stream: InputStream
    ) : Closeable {
        companion object {
            /**
             * TODO
             */
            fun fromString(value: String): Body = Body(value.byteInputStream())

            /**
             * TODO
             */
            fun fromFile(file: File): Body = Body(file.inputStream())
        }

        /**
         * TODO
         */
        fun <R> useStream(block: (InputStream) -> R): R = use {
            block(stream)
        }

        /**
         * TODO
         */
        override fun close() {
            try {
                stream.close()
            } catch (e: IOException) {
                // Ignore
            }
        }
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
}
