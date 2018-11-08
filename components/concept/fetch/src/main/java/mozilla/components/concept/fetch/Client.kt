/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.fetch

import java.io.IOException

/**
 * A generic [Client] for fetching resources via HTTP/s.
 *
 * Abstract base class / interface for clients implementing the `concept-fetch` component.
 *
 * The [Request]/[Response] API is inspired by the Web Fetch API:
 * https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API
 */
abstract class Client {
    /**
     * Starts the process of fetching a resource from the network as described by the [Request] object.
     *
     * A [Response] may keep references to open streams. Therefore it's important to always close the [Response] or
     * its [Response.Body].
     *
     * Use the `use()` extension method when performing multiple operations on the [Response] object:
     *
     * ```Kotlin
     * client.fetch(request).use { response ->
     *     // Use response. Resources will get released automatically at the end of the block.
     * }
     * ```
     *
     * Alternatively you can use multiple `use*()` methods on the [Response.Body] object.
     *
     * @param request The request to be executed by this [Client].
     * @return The [Response] returned by the server.
     * @throws IOException if the request could not be executed due to cancellation, a connectivity problem or a timeout.
     */
    @Throws(IOException::class)
    abstract fun fetch(request: Request): Response

    /**
     * Creates a "base" user agent to be used by implementations of `concept-fetch`. Implementations may append additional
     * text to the user agent.
     */
    protected fun createBaseUserAgent(): String {
        return "MozacFetch/${BuildConfig.LIBRARY_VERSION}"
    }
}
