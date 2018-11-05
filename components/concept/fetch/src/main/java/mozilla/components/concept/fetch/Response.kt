/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.fetch

import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

/**
 * TODO
 */
data class Response(
    val url: String,
    val status: Int,
    val headers: Headers,
    val body: Body
): Closeable {
    /**
     * TODO
     *
     * **The response body can be consumed only once.**.
     */
    open class Body(
        private val stream: InputStream
    ): Closeable, AutoCloseable {
        /**
         * TODO:
         */
        fun <R> useStream(block: (InputStream) -> R): R = use {
            block(stream)
        }

        /**
         * TODO:
         */
        fun <R> useBufferedReader(block: (BufferedReader) -> R): R = use {
            block(stream.bufferedReader())
        }

        /**
         * TODO:
         */
        fun string(charset: Charset = Charsets.UTF_8): String = use {
            stream.bufferedReader(charset).readText()
        }

        /**
         * TODO:
         */
        override fun close() {
            try {
                stream.close()
            } catch (e: IOException) {
                // Ignore
            }
        }

        companion object {
            /**
             * TODO:
             */
            fun empty() = Body("".byteInputStream())
        }
    }

    /**
     * TODO
     */
    override fun close() {
        body.close()
    }
}

/**
 * Returns true if the response was successful (status in the range 200-299) or false otherwise.
 */
val Response.success: Boolean
    get() = status in 200..299

/**
 * Returns true if the response was a client error (status in the range 400-499) or false otherwise.
 */
val Response.clientError: Boolean
    get() = status in 400..499