package mozilla.components.feature.awesomebar.provider

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import mozilla.components.browser.search.SearchEngine
import mozilla.components.browser.search.suggestions.SearchSuggestionClient
import mozilla.components.concept.awesomebar.AwesomeBar
import mozilla.components.feature.search.SearchUseCases
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class SearchSuggestionProvider(
    private val searchEngine: SearchEngine,
    private val searchUseCase: SearchUseCases.DefaultSearchUrlUseCase
): AwesomeBar.SuggestionProvider {
    private var client = if (searchEngine.canProvideSearchSuggestions) {
        SearchSuggestionClient(searchEngine, { url -> fetch(url) })
    } else {
        null
    }

    override fun onInputChanged(text: String): List<AwesomeBar.Suggestion> {
        if (text.isEmpty()) {
            return emptyList()
        }

        val client = client ?: return emptyList()

        val searchSuggestions = async { client.getSuggestions(text) }

        val chips = mutableListOf<AwesomeBar.Suggestion.Chip>()

        runBlocking {
            searchSuggestions.await()?.forEach { title ->
                chips.add(AwesomeBar.Suggestion.Chip(title))
            }
        }

        return listOf(AwesomeBar.Suggestion(
            title = searchEngine.name,
            chips = chips,
            onChipClicked = { chip ->
                searchUseCase.invoke(chip.title)
            }
        ))
    }
}

// To be replaced with http/fetch dependency:
// https://github.com/mozilla-mobile/android-components/issues/1012
private fun fetch(url: String): String? {
    var urlConnection: HttpURLConnection? = null
    try {
        urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.useCaches = false
        urlConnection.readTimeout = 2000
        urlConnection.connectTimeout = 1000

        val responseCode = urlConnection.responseCode
        if (responseCode !in 200..299) {
            return null
        }

        return urlConnection.inputStream.bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        return null
    } catch (e: ClassCastException) {
        return null
    } catch (e: ArrayIndexOutOfBoundsException) {
        // On some devices we are seeing an ArrayIndexOutOfBoundsException being thrown
        // somewhere inside AOSP/okhttp.
        // See: https://github.com/mozilla-mobile/android-components/issues/964
        return null
    } finally {
        urlConnection?.disconnect()
    }
}
