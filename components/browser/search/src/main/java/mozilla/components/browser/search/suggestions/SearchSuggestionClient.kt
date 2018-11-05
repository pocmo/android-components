/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.search.suggestions

import kotlinx.coroutines.experimental.async
import mozilla.components.browser.search.SearchEngine
import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.Request
import org.json.JSONException
import java.io.IOException

/**
 *  Provides an interface to get search suggestions from a given SearchEngine.
 */
class SearchSuggestionClient(
    private val searchEngine: SearchEngine,
    private val client: Client
) {

    /**
     * Exception types for errors caught while getting a list of suggestions
     */
    class FetchException : Exception("There was a problem fetching suggestions")
    class ResponseParserException : Exception("There was a problem parsing the suggestion response")

    init {
        if (!searchEngine.canProvideSearchSuggestions) {
            throw IllegalArgumentException("SearchEngine does not support search suggestions!")
        }
    }

    /**
     * Gets search suggestions for a given query
     */
    suspend fun getSuggestions(query: String): List<String>? {
        val suggestionsURL = searchEngine.buildSuggestionsURL(query) ?: return null

        val parser = selectResponseParser(searchEngine)

        val suggestionResults = try {
            async {
                client.fetch(Request(suggestionsURL)).use { response ->
                    response.body.string()
                }
            }
        } catch (_: IOException) {
            throw FetchException()
        }

        return try {
            parser(suggestionResults.await())
        } catch (_: JSONException) {
            throw ResponseParserException()
        }
    }
}
