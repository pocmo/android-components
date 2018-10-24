/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.awesomebar

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import mozilla.components.concept.awesomebar.AwesomeBar

class BrowserAwesomeBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), AwesomeBar {
    private val dispatcher = newFixedThreadPoolContext(3, "AwesomeBarProviders")
    private val suggestionsAdapter = SuggestionsAdapter(this)
    private val providers: MutableList<AwesomeBar.SuggestionProvider> = mutableListOf()
    private var jobs: List<Job> = emptyList()
    internal var listener: (() -> Unit)? = null

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = suggestionsAdapter
    }

    @Synchronized
    override fun addProviders(vararg providers: AwesomeBar.SuggestionProvider) {
        this.providers.addAll(providers)
    }

    @Synchronized
    override fun onInputStarted() {
        providers.forEach { provider -> provider.onInputStarted() }
    }

    @Synchronized
    override fun onInputChanged(text: String) {
        jobs.forEach { job -> job.cancel() }

        suggestionsAdapter.clearSuggestions()

        jobs = providers.map { provider ->
            launch(UI) {
                val suggestions = async(dispatcher) { provider.onInputChanged(text) }
                suggestionsAdapter.addSuggestions(suggestions.await())
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        dispatcher.close()
    }

    @Synchronized
    override fun onInputCancelled() {
        providers.forEach { provider -> provider.onInputCancelled() }
    }

    override fun setOnStopListener(listener: () -> Unit) {
        this.listener = listener
    }
}
